package ru.kusok_piroga.gorzdravbot.producer.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.*;
import ru.kusok_piroga.gorzdravbot.producer.utils.TaskValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private final Map<TaskState, TaskFieldFiller> taskFillers = Map.of(
            SET_DISTRICT, new DistrictFiller(),
            SET_POLYCLINIC, new PolyclinicFiller(),
            SET_SPECIALITY, new SpecialityFiller(),
            SET_DOCTOR, new DoctorFiller(),
            SET_PATIENT, new PatientFiller(),
            SET_TIME_LOW_LIMITS, new LowTimeLimitFiller(),
            SET_TIME_HIGH_LIMITS, new HighTimeLimitFiller(),
            SET_DATE_LIMITS, new DateFiller()
    );

    public List<TaskEntity> getCompletedTaskList(long chatId) {
        return repository.findAllCompletedTasksByDialogId(chatId);
    }

    public List<TaskEntity> getUncompletedTaskList(long chatId) {
        return repository.findAllUncompletedTasksByDialogId(chatId);
    }

    public void deleteTask(String taskIdStr) throws WrongIdException {
        try {
            deleteTask(Long.parseLong(taskIdStr));
        } catch (NumberFormatException e) {
            throw new WrongIdException();
        }
    }

    public void deleteTask(long taskId) {
        repository.deleteById(taskId);
    }

    /**
     * @param taskId task id
     * @return Canceled appointment id
     * @throws NoSuchElementException if task not found
     * @throws CancelAppointmentException if cancel failed
     */
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

    public void createTask(long dialogId) {
        clearUncompletedTask(dialogId);

        TaskEntity task = new TaskEntity();

        task.setDialogId(dialogId);
        task.setState(SET_DISTRICT);

        repository.save(task);
    }

    private void clearUncompletedTask(long dialogId) {
        repository.deleteByDialogIdAndStateIsNot(dialogId, SETUPED);
    }

    public TaskEntity fillTaskFields(TaskEntity task, String value) throws Exception {
        TaskFieldFiller filler = taskFillers.get(task.getState());
        if (filler == null) {
            return task;
        }
        return filler.fill(task, value);
    }

    public TaskEntity getUnsetupedTaskByDialog(Long dialogId){
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
            task.setState(TaskState.SET_TIME_LOW_LIMITS);

            return repository.save(task);
        }
    }

    private class LowTimeLimitFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws TimeFormatException {
            if (TaskValidator.validateTime(value)) {
                task.setState(SET_TIME_HIGH_LIMITS);
                task.setLowTimeLimit(value);
                return repository.save(task);
            } else {
                throw new TimeFormatException();
            }
        }
    }

    private class HighTimeLimitFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws TimeFormatException, TimeConsistencyException {
            if (TaskValidator.validateTime(value)) {
                task.setHighTimeLimit(value);

                if (!TaskValidator.validateTaskTimeLimits(task)) {
                    task.setState(SET_TIME_LOW_LIMITS);
                    repository.save(task);
                    throw new TimeConsistencyException();
                }

                task.setState(SET_DATE_LIMITS);
                return repository.save(task);
            } else {
                throw new TimeFormatException();
            }
        }
    }

    private class DateFiller implements TaskFieldFiller {
        @Override
        public TaskEntity fill(TaskEntity task, String value) throws DateFormatException {
            SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
            formater.setLenient(false);
            try {
                task.setHighDateLimit(formater.parse(value));
                task.setState(SETUPED);
                return repository.save(task);
            } catch (ParseException e) {
                throw  new DateFormatException();
            }
        }
    }
}