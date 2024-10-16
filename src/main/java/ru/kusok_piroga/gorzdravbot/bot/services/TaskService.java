package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
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
            case SET_DISTRICT -> {
                return taskScenarioSetPolyclinic(task, message);
            }
        }

        return new GenericTelegramResponse("Если что-то пошло не так, вы можете начать создание задачи заново с помощью " + Commands.COMMAND_ADD_TASK);
    }

    private TelegramResponse taskScenarioSetPolyclinic(TaskEntity task, String message) {
        Integer districtId = Integer.parseInt(message);
        task.setDistrictId(districtId);
        task.setState(TaskState.SET_POLYCLINIC);
        repository.save(task);
        return printPolyclinics(districtId);
    }

    private TelegramResponse printPolyclinics(Integer distrinctId) {
        String answerText = "Выберите мед. учреждение:";
        List<Map<String, String>> buttons = new LinkedList<>();
        for (Polyclinic polyclinic : api.getPolyclinicsByDistrict(distrinctId)) {
            buttons.add(new HashMap<>());
            buttons.get(buttons.size() - 1).put(polyclinic.lpuFullName(), polyclinic.id().toString());
        }

        return (buttons.isEmpty()) ?
                new GenericTelegramResponse("Мед. учреждения не найдены")
                :
                new InlineButtonTelegramResponse(answerText, buttons);
    }
}
