package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Doctor;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.models.Specialty;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskState;
import ru.kusok_piroga.gorzdravbot.bot.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.common.InlineButtonTelegramResponse;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService implements ICommandService {

    private final ApiService api;
    private final TaskRepository repository;

    @Override
    public TelegramResponse execute(UpdateRequest request) {

        Long dialogId = request.getChatId();

        Optional<TaskEntity> result = repository.findFirstByDialogIdAndStateIsNot(dialogId, TaskState.FINAL);

        if (result.isEmpty()) {
            return taskCreate(dialogId);
        }

        return taskScenario(result.get(), request.getText());
    }

    public TelegramResponse cleanStart(UpdateRequest request) {
        Long dialogId = request.getChatId();
        repository.deleteByDialogIdAndStateIsNot(dialogId, TaskState.FINAL);
        return taskCreate(dialogId);
    }

    private TelegramResponse taskCreate(long dialogId) {
        TaskEntity task = new TaskEntity();
        task.setDialogId(dialogId);
        task.setState(TaskState.SET_DISTRICT);
        repository.save(task);

        String answerText = "Выберите район:";
        List<Map<String, String>> buttons = new ArrayList<>();

        for (District district : api.getDistricts()) {
            buttons.add(new HashMap<>());
            buttons.get(buttons.size() - 1).put(district.getName(), district.getId().toString());
        }

        return new InlineButtonTelegramResponse(answerText, buttons);
    }

    private TelegramResponse taskScenario(TaskEntity task, String message) {

        switch (task.getState()) {
            case SET_PATIENT -> {
                return taskScenarioSetPatient(task, message);
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
        return null;
    }

    private TelegramResponse taskScenarioSetPatient(TaskEntity task, String message) {
        return null;
    }

    private TelegramResponse printPolyclinics(Integer distrinctId) {
        String answerText = "Выберите мед. учреждение:";
        List<Map<String, String>> buttons = new LinkedList<>();
        for (Polyclinic polyclinic : api.getPolyclinicsByDistrict(distrinctId)) {
            buttons.add(new HashMap<>());
            buttons.get(buttons.size() - 1).put(polyclinic.lpuFullName() , polyclinic.id().toString());
        }

        return (buttons.isEmpty()) ?
                new GenericTelegramResponse("Мед. учреждения не найдены")
                :
                new InlineButtonTelegramResponse(answerText, buttons);
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
