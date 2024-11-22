package ru.kusok_piroga.gorzdravbot.producer.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.utils.TaskValidator;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.kusok_piroga.gorzdravbot.domain.models.TaskState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final ApiService api;
    private final TaskRepository repository;
    private final PatientService patientService;

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

    public void createTask(long dialogId) throws SaveException {
        clearUncompletedTask(dialogId);

        TaskEntity task = new TaskEntity();

        task.setDialogId(dialogId);
        task.setState(SET_DISTRICT);

        try {
            repository.save(task);
        } catch (Exception e) {
            throw new SaveException();
        }
    }

    private void clearUncompletedTask(long dialogId) {
        repository.deleteByDialogIdAndStateIsNot(dialogId, SETUPED);
    }

    public TaskEntity fillTaskFields(TaskEntity task, String value) throws TimeFormatException, TimeConsistencyException, DateFormatException, WrongPolyclinicForPatientException, NoSuchElementException {
        return switch (task.getState()) {
            case INIT, SETUPED -> task;
            case SET_DISTRICT -> {
                Integer districtId = Integer.parseInt(value);
                task.setDistrictId(districtId);
                task.setState(SET_POLYCLINIC);
                yield repository.save(task);
            }
            case SET_POLYCLINIC -> {
                Integer polyclinicId = Integer.parseInt(value);
                task.setPolyclinicId(polyclinicId);
                task.setState(SET_SPECIALITY);
                yield repository.save(task);
            }
            case SET_SPECIALITY -> {
                Integer specialityId = Integer.parseInt(value);
                task.setSpecialityId(specialityId);
                task.setState(SET_DOCTOR);
                yield repository.save(task);
            }
            case SET_DOCTOR -> {
                task.setDoctorId(value);
                task.setState(SET_PATIENT);
                yield repository.save(task);
            }
            case SET_PATIENT -> {
                task.setState(SET_TIME_LOW_LIMITS);
                yield setPatient(task, value);
            }
            case SET_TIME_LOW_LIMITS -> {
                if (TaskValidator.validateTime(value)) {
                    task.setState(SET_TIME_HIGH_LIMITS);
                    task.setLowTimeLimit(value);
                    yield repository.save(task);
                } else {
                    throw new TimeFormatException();
                }
            }
            case SET_TIME_HIGH_LIMITS -> {
                if (TaskValidator.validateTime(value)) {
                    task.setHighTimeLimit(value);

                    if (!TaskValidator.validateTaskTimeLimits(task)) {
                        task.setState(SET_TIME_LOW_LIMITS);
                        repository.save(task);
                        throw new TimeConsistencyException();
                    }

                    task.setState(SET_DATE_LIMITS);
                    yield repository.save(task);
                } else {
                    throw new TimeFormatException();
                }
            }
            case SET_DATE_LIMITS -> {
                task.setState(SETUPED);
                yield setDate(task, value);
            }


        };
    }

    private TaskEntity setPatient(TaskEntity task, String value) throws WrongPolyclinicForPatientException, NoSuchElementException {
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
        return repository.save(task);
    }

    private TaskEntity setDate(TaskEntity task, String value) throws DateFormatException {
        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        formater.setLenient(false);
        try {
            task.setHighDateLimit(formater.parse(value));
            return repository.save(task);
        } catch (ParseException e) {
            throw  new DateFormatException();
        }
    }

    public TaskEntity getUnsetupedTaskByDialog(Long dialogId){
        return repository.findFirstByDialogIdAndStateIsNot(dialogId, TaskState.SETUPED).orElseThrow();
    }
}
