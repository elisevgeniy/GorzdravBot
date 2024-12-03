package ru.kusok_piroga.gorzdravbot.bot.services;

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
import ru.kusok_piroga.gorzdravbot.bot.responses.InlineButtonTelegramResponse;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.TimeConsistencyException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.TimeFormatException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.WrongPolyclinicForPatientException;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskCreateCommandService implements ICommandService {

    private final ApiService api;
    private final TaskService taskService;
    private final PatientService patientService;
    private final PatientListCommandService patientListCommandService;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {

        long dialogId = request.getChatId();

        if (patientService.getPatientList(dialogId).isEmpty()) {
            return new GenericTelegramResponse("Для добавления задачи требуется заранее добавить пациента с помощью команды " + Commands.COMMAND_ADD_PATIENT);
        }

        taskService.createTask(dialogId);

        return printDistricts();
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        TaskEntity task;
        try {
            task = taskService.getUnsetupedTaskByDialog(request.getChatId());
        } catch (NoSuchElementException e){
            return new GenericTelegramResponse("Начать создание задачи можно помощью " + Commands.COMMAND_ADD_TASK);
        }

        try {
            taskService.fillTaskFields(task, request.getText());

        } catch (TimeFormatException e) {
            throw new ru.kusok_piroga.gorzdravbot.bot.exceptions.TimeFormatException();
        } catch (TimeConsistencyException e) {
            return new GenericTelegramResponse("Неверный диапазон времени, попробуйте снова с нижней границы");
        } catch (DateFormatException e) {
            throw new ru.kusok_piroga.gorzdravbot.bot.exceptions.DateFormatException();
        } catch (WrongPolyclinicForPatientException e) {
            return new CompositeTelegramResponse(List.of(
                    new GenericTelegramResponse("Пациент в мед. учреждении не найден. Выбери другого"),
                    patientListCommandService.printPatientListForChoose(task.getDialogId())
            ));
        } catch (NoSuchElementException e){
            return new GenericTelegramResponse("Для добавления задачи требуется заранее добавить пациента с помощью команды " + Commands.COMMAND_ADD_PATIENT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return switch (task.getState()) {
            case INIT -> null;
            case SET_DISTRICT -> printDistricts();
            case SET_POLYCLINIC -> printPolyclinics(task.getDistrictId());
            case SET_SPECIALITY -> printSpecialities(task.getPolyclinicId());
            case SET_DOCTOR -> printDoctors(task.getPolyclinicId(), task.getSpecialityId());
            case SET_TIME_LIMITS -> printTimeLimits();
            case SET_DATE_LIMITS -> printDateLimit();
            case SET_PATIENT -> patientListCommandService.printPatientListForChoose(task.getDialogId());
            case SETUPED -> new GenericTelegramResponse("Задача создана. Список задач: " + Commands.COMMAND_LIST_TASK);
        };
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
                Пришлите одним сообщением на разных строках:
                1. Диапазон времени, в котором искать номерки в формате
                чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию 00:00 - 23:59)
                2. Диапазон времени, в котором НЕ искать номерки в формате
                !чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию не будет установлен)
                Для пропуска настройки пришлите "дальше" """;
        return new GenericTelegramResponse(answerText);
    }

    private TelegramResponse printDateLimit() {
        String answerText = """
                Пришлите дату до которой искать номерки в формате
                дд.мм.гггг
                или диапазон дат, в котором искать номерки в формате
                дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг
                или диапазон дат, в котором искать и НЕ номерки в формате
                дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг !дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг""";
        return new GenericTelegramResponse(answerText);
    }
}
