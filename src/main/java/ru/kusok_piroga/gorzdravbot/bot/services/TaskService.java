package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.StreamTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Doctor;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.models.Specialty;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.common.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.common.models.TaskState;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.common.responses.InlineButtonTelegramResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService implements ICommandService {

    private final ApiService api;
    private final PatientListService patientListService;
    private final TaskRepository repository;

    @Override
    public TelegramResponse execute(UpdateRequest request) {

        Long dialogId = request.getChatId();

        Optional<TaskEntity> result = repository.findFirstByDialogIdAndStateIsNot(dialogId, TaskState.FINAL);

        if (result.isEmpty()) {
            if (request.getMessageTypes().contains(MessageType.COMMAND)) {
                return taskCreate(dialogId);
            } else {
                return null;
            }
        }

        return taskScenario(result.get(), request.getText());
    }

    public TelegramResponse cleanStart(UpdateRequest request) {
        Long dialogId = request.getChatId();
        repository.deleteByDialogIdAndStateIsNot(dialogId, TaskState.FINAL);
        return taskCreate(dialogId);
    }

    private TelegramResponse taskCreate(long dialogId) {
        if (patientListService.getPatientList(dialogId).isEmpty()) {
            return new GenericTelegramResponse("Для добавления задачи требуется заранее добавить пациента с помощью команды " + Commands.COMMAND_ADD_PATIENT);
        }

        TaskEntity task = new TaskEntity();
        task.setDialogId(dialogId);
        task.setState(TaskState.SET_DISTRICT);
        repository.save(task);
        return printDistricts();
    }

    private TelegramResponse taskScenario(TaskEntity task, String message) {

        switch (task.getState()) {
            case INIT -> {
                return null;
            }
            case SET_DISTRICT -> {
                return taskScenarioSetDistrict(task, message);
            }
            case SET_POLYCLINIC -> {
                return taskScenarioSetPolyclinic(task, message);
            }
            case SET_SPECIALITY -> {
                return taskScenarioSetSpeciality(task, message);
            }
            case SET_DOCTOR -> {
                return taskScenarioSetDoctor(task, message);
            }
            case SET_TIME_LIMITS -> {
                return taskScenarioSetTimeLimits(task, message);
            }
            case SET_PATIENT -> {
                return taskScenarioSetPatient(task, message);
            }
            case FINAL -> {
                return null;
            }
        }

        return new GenericTelegramResponse("Если что-то пошло не так, вы можете начать создание задачи заново с помощью " + Commands.COMMAND_ADD_TASK);
    }

    private TelegramResponse taskScenarioSetDistrict(TaskEntity task, String message) {
        Integer districtId = Integer.parseInt(message);
        task.setDistrictId(districtId);
        task.setState(TaskState.SET_POLYCLINIC);
        repository.save(task);
        return printPolyclinics(districtId);
    }

    private TelegramResponse taskScenarioSetPolyclinic(TaskEntity task, String message) {
        Integer polyclinicId = Integer.parseInt(message);
        task.setPolyclinicId(polyclinicId);
        task.setState(TaskState.SET_SPECIALITY);
        repository.save(task);
        return printSpecialities(polyclinicId);
    }

    private TelegramResponse taskScenarioSetSpeciality(TaskEntity task, String message) {
        Integer specialityId = Integer.parseInt(message);
        task.setSpecialityId(specialityId);
        task.setState(TaskState.SET_DOCTOR);
        repository.save(task);
        return printDoctors(task.getPolyclinicId(), specialityId);
    }

    private TelegramResponse taskScenarioSetDoctor(TaskEntity task, String doctorId) {
        task.setDoctorId(doctorId);
        task.setState(TaskState.SET_TIME_LIMITS);
        repository.save(task);
        return printTimeLimits();
    }

    private TelegramResponse taskScenarioSetTimeLimits(TaskEntity task, String message) {
        if (message.length() == 5) {
            return taskScenarioSetTime(task, message);
        } else if (message.length() == 10) {
            return taskScenarioSetDate(task, message);
        }
        return new GenericTelegramResponse("Ошибка формата, попробуйте ещё раз");
    }

    private TelegramResponse taskScenarioSetTime(TaskEntity task, String message) {
        if (TaskValidator.validateTime(message)) {
            if (task.getLowTimeLimit() == null) {
                task.setLowTimeLimit(message);
                repository.save(task);
                return new GenericTelegramResponse("Минимальное время записи установлено на %s%n Введите максимальное время:".formatted(message));
            } else if (task.getHighTimeLimit() == null) {

                task.setHighTimeLimit(message);

                if (!TaskValidator.validateTaskTimeLimits(task)) {
                    TelegramResponse response = new GenericTelegramResponse("Лимиты были заданы неверно (min = %s, max = %s). Лимиты сброшены, установите заново.".formatted(task.getLowTimeLimit(), task.getHighTimeLimit()));
                    task.setLowTimeLimit(null);
                    task.setHighTimeLimit(null);
                    repository.save(task);
                    return response;
                }

                repository.save(task);

                return new GenericTelegramResponse("Максимальное время записи установлено на %s%n Введите дату, до которой будут отслеживаться номерки:".formatted(message));
            }
        }
        return new GenericTelegramResponse("Ошибка формата времени, попробуйте ещё раз");
    }

    private TelegramResponse taskScenarioSetDate(TaskEntity task, String message) {
        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        formater.setLenient(false);
        try {
            task.setHighDateLimit(formater.parse(message));
            task.setState(TaskState.SET_PATIENT);
            repository.save(task);
            return new CompositeTelegramResponse(List.of(
                    new GenericTelegramResponse("Крайняя дата для записи - %s".formatted(message)),
                    patientListService.printPatientList(task.getDialogId())
            ));
        } catch (ParseException e) {
            return new GenericTelegramResponse("Ошибка формата даты, попробуйте ещё раз");
        }
    }

    private TelegramResponse taskScenarioSetPatient(TaskEntity task, String message) {

        Optional<PatientEntity> patient = patientListService.getPatientById(Long.parseLong(message));

        if (patient.isEmpty()){
            return new GenericTelegramResponse("Пациент не найден. Всё, кирдык, давай по-новой, но добавь пациента с помощью " + Commands.COMMAND_ADD_PATIENT);
        }

        String patientId = api.getPatientId(
                task.getPolyclinicId(),
                patient.get().getFirstName(),
                patient.get().getSecondName(),
                patient.get().getMiddleName(),
                patient.get().getBirthday()
        );

        if (patientId.isEmpty()){
            return new CompositeTelegramResponse(List.of(
                    new GenericTelegramResponse("Пациент в мед. учреждении не найден. Выбери другого"),
                    patientListService.printPatientList(task.getDialogId())
            ));
        }

        patient.get().setPatientId(patientId);
        patientListService.savePatient(patient.get());

        task.setPatientEntity(patient.get());
        task.setState(TaskState.FINAL);
        repository.save(task);

        return new GenericTelegramResponse("Задача создана");
    }

    private TelegramResponse printDistricts() {
        String answerText = "Выберите район:";
        List<Map<String, String>> buttons = new ArrayList<>();

        for (District district : api.getDistricts()) {
            if (buttons.isEmpty() || buttons.get(buttons.size() - 1).size() == 2) {
                buttons.add(new TreeMap<>());
            }
            buttons.get(buttons.size() - 1).put(district.getName(), district.getId().toString());
        }

        return new InlineButtonTelegramResponse(answerText, buttons);
    }

    private TelegramResponse printPolyclinics(Integer distrinctId) {
        List<Polyclinic> polyclinics = api.getPolyclinicsByDistrict(distrinctId);
        if (polyclinics.isEmpty()) {
            return new GenericTelegramResponse("Мед. учреждения не найдены");
        }

        return new CompositeTelegramResponse(List.of(
                new StreamTelegramResponse(polyclinics.stream().map(polyclinic ->
                        "%s - %s%n(%s)".formatted(
                                polyclinic.id().toString(),
                                polyclinic.lpuFullName(),
                                polyclinic.address()
                        )
                )),
                new GenericTelegramResponse("Напишите номер мед.  учреждения:\n(можно использовать поиск)")
        ));
    }

    private TelegramResponse printSpecialities(Integer polyclinicId) {
        String answerText = "Выберите специальность врача:";
        List<Map<String, String>> buttons = new LinkedList<>();
        for (Specialty specialty : api.getSpecialties(polyclinicId)) {
            buttons.add(new HashMap<>());
            buttons.get(buttons.size() - 1).put(specialty.name(), specialty.id());
        }

        return (buttons.isEmpty()) ?
                new GenericTelegramResponse("Специальности не найдены")
                :
                new InlineButtonTelegramResponse(answerText, buttons);
    }

    private TelegramResponse printDoctors(Integer polyclinicId, Integer specialtyId) {
        String answerText = "Выберите врача:";
        List<Map<String, String>> buttons = new LinkedList<>();
        for (Doctor doctor : api.getDoctors(polyclinicId, specialtyId.toString())) { // todo убрать .toString
            buttons.add(new HashMap<>());
            buttons.get(buttons.size() - 1).put(doctor.name(), doctor.id());
        }

        return (buttons.isEmpty()) ?
                new GenericTelegramResponse("Врачи не найдены")
                :
                new InlineButtonTelegramResponse(answerText, buttons);
    }

    private TelegramResponse printTimeLimits() {
        String answerText = """
                Последовательно пришлите 3 сообщения:
                "1. Нижний предел записи чч:мм
                "2. Верхний предел записи чч:мм
                "3. Дату до которой искать номерки дд.мм.гггг""";

        return new GenericTelegramResponse(answerText);
    }
}
