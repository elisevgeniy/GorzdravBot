package ru.kusok_piroga.gorzdravbot.producer.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.*;
import ru.kusok_piroga.gorzdravbot.domain.repositories.SkipAppointmentRepository;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static ru.kusok_piroga.gorzdravbot.domain.models.TaskState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final ApiService api;
    private final TaskRepository repository;
    private final PatientService patientService;
    private final SkipAppointmentRepository skipAppointmentRepository;

    private final Map<TaskState, TaskFieldFiller> taskFillers = Map.of(
            SET_DISTRICT, new DistrictFiller(),
            SET_POLYCLINIC, new PolyclinicFiller(),
            SET_SPECIALITY, new SpecialityFiller(),
            SET_DOCTOR, new DoctorFiller(),
            SET_PATIENT, new PatientFiller(),
            SET_TIME_LIMITS, new TimeLimitFiller(),
            SET_DATE_LIMITS, new DateLimitFiller()
    );

    @Transactional
    public List<TaskEntity> getCompletedTaskList(long chatId) {
        return repository.findAllCompletedTasksByDialogId(chatId);
    }

    @Transactional
    public List<TaskEntity> getUncompletedTaskList(long chatId) {
        return repository.findAllUncompletedTasksByDialogId(chatId);
    }

    @Transactional
    public void deleteTask(String taskIdStr) throws WrongIdException {
        try {
            deleteTask(Long.parseLong(taskIdStr));
        } catch (NumberFormatException e) {
            throw new WrongIdException();
        }
    }

    @Transactional
    public void deleteTask(long taskId) {
        repository.deleteById(taskId);
    }

    /**
     * @param taskId task id
     * @return Canceled appointment id
     * @throws NoSuchElementException     if task not found
     * @throws CancelAppointmentException if cancel failed
     */
    @Transactional(rollbackOn = Exception.class)
    public String cancelAppointmentByTask(Long taskId) throws NoSuchElementException, CancelAppointmentException {
        TaskEntity task = repository.findById(taskId).orElseThrow();

        if (api.cancelAppointment(
                task.getPolyclinicId(),
                task.getRecordedAppointmentId(),
                task.getPatientEntity().getPatientId()
        )) {
            String canceledAppointmentId = task.getRecordedAppointmentId();
            task.setRecordedAppointmentId(null);
            repository.save(task);
            return canceledAppointmentId;
        } else {
            log.warn("Cancel fail. Task id = {}, Appointment id = {}", taskId, task.getRecordedAppointmentId());
            throw new CancelAppointmentException();
        }
    }

    @Transactional
    public void createTask(long dialogId) {
        clearUncompletedTask(dialogId);

        TaskEntity task = new TaskEntity();

        task.setDialogId(dialogId);
        task.setState(SET_DISTRICT);

        repository.save(task);
    }

    @Transactional
    protected void clearUncompletedTask(long dialogId) {
        repository.deleteByDialogIdAndStateIsNot(dialogId, SETUPED);
    }

    @Transactional(rollbackOn = Exception.class)
    public TaskEntity fillTaskFields(TaskEntity task, String value) throws Exception {
        TaskFieldFiller filler = taskFillers.get(task.getState());
        if (filler == null) {
            return task;
        }
        return filler.fill(task, value);
    }

    @Transactional
    public TaskEntity getUnsetupedTaskByDialog(Long dialogId) {
        return repository.findFirstByDialogIdAndStateIsNot(dialogId, TaskState.SETUPED).orElseThrow();
    }

    private interface TaskFieldFiller {
        TaskEntity fill(TaskEntity task, String value) throws Exception;
    }

    private class DistrictFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) {
            task.setDistrictId(Integer.parseInt(value));
            task.setState(TaskState.SET_POLYCLINIC);
            return repository.save(task);
        }
    }

    private class PolyclinicFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) {
            task.setPolyclinicId(Integer.parseInt(value));
            task.setState(TaskState.SET_SPECIALITY);
            return repository.save(task);
        }
    }

    private class SpecialityFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) {
            task.setSpecialityId(Integer.parseInt(value));
            task.setState(TaskState.SET_DOCTOR);
            return repository.save(task);
        }
    }

    private class DoctorFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) {
            task.setDoctorId(value);
            task.setState(TaskState.SET_PATIENT);
            return repository.save(task);
        }
    }

    private class PatientFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws WrongPolyclinicForPatientException {
            Optional<PatientEntity> patient = patientService.getPatientById(Long.parseLong(value));

            if (patient.isEmpty()) {
                throw new NoSuchElementException();
            }

            String patientId = api.getPatientId(
                    task.getPolyclinicId(),
                    patient.get().getFirstName(),
                    patient.get().getSecondName(),
                    patient.get().getMiddleName(),
                    patient.get().getBirthday()
            );

            if (patientId.isEmpty()) {
                throw new WrongPolyclinicForPatientException();
            }

            patient.get().setPatientId(patientId);
            patientService.savePatient(patient.get());

            task.setPatientEntity(patient.get());
            task.setState(TaskState.SET_TIME_LIMITS);

            return repository.save(task);
        }
    }

    private class TimeLimitFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws TimeFormatException {
            try {
                task.setState(SET_DATE_LIMITS);
                if (!value.trim().equalsIgnoreCase("дальше")) {
                    task.setTimeLimits(new TaskTimeLimits(value));
                }
                return repository.save(task);
            } catch (TimeLimitParseException e) {
                throw new TimeFormatException();
            }
        }
    }

    private class DateLimitFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws DateFormatException {
            try {
                task.setDateLimits(new TaskDateLimits(value));
                task.setState(SETUPED);
                return repository.save(task);
            } catch (DateLimitParseException e) {
                throw new DateFormatException();
            }
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean skipAppointment(Long taskId, String appointmentId) {

        Optional<TaskEntity> task = repository.findById(taskId);

        if (task.isEmpty()) return false;

        SkipAppointmentEntity skipAppointment = new SkipAppointmentEntity();
        skipAppointment.setAppointmentId(appointmentId);
        skipAppointment.setTask(task.get());

        skipAppointmentRepository.save(skipAppointment);

        return true;
    }

    @Transactional
    public boolean restartTask(long taskId) {
        Optional<TaskEntity> task = repository.findById(taskId);
        if (task.isEmpty()) return false;

        task.get().setLastNotify(null);
        task.get().setRecordedAppointmentId(null);
        task.get().setCompleted(false);

        repository.save(task.get());

        return true;
    }

    @Transactional
    public boolean changeTime(long taskId, TaskTimeLimits timeLimits) {
        Optional<TaskEntity> task = repository.findById(taskId);
        if (task.isEmpty()) {
            log.warn("Change time fail. Task id = {} not found", taskId);
            return false;
        }

        task.get().setTimeLimits(timeLimits);

        repository.save(task.get());
        return true;
    }

    @Transactional
    public boolean changeDate(long taskId, TaskDateLimits dateLimits) {
        Optional<TaskEntity> task = repository.findById(taskId);
        if (task.isEmpty()) {
            log.warn("Change date fail. Task id = {} not found", taskId);
            return false;
        }

        task.get().setDateLimits(dateLimits);

        repository.save(task.get());
        return true;
    }

    @Transactional
    public boolean validateTaskIdByDialogId(Long taskId, Long dialogId){
        return repository.validateTaskByDialog(taskId, dialogId);
    }

    @Transactional
    public boolean copyTask(Long taskId){
        Optional<TaskEntity> task = repository.findById(taskId);
        if (task.isEmpty()) {
            log.warn("Copy task fail. Task id = {} not found", taskId);
            return false;
        }

        TaskEntity newTask = new TaskEntity();
        TaskEntity sourceTask = task.get();

        newTask.setDialogId(sourceTask.getDialogId());
        newTask.setDistrictId(sourceTask.getDistrictId());
        newTask.setPolyclinicId(sourceTask.getPolyclinicId());
        newTask.setSpecialityId(sourceTask.getSpecialityId());
        newTask.setDoctorId(sourceTask.getDoctorId());
        newTask.setPatientEntity(sourceTask.getPatientEntity());
        newTask.setTimeLimits(sourceTask.getTimeLimits());
        newTask.setDateLimits(sourceTask.getDateLimits());
        newTask.setState(SETUPED);

        repository.save(newTask);
        return true;
    }
}
