package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
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

            TaskEntity task = new TaskEntity();
            task.setDialogId(dialogId);
            task.setState(TaskState.SET_DISTRICT);
            repository.save(task);

            StringBuilder answerText = new StringBuilder();
            answerText.append("Выберите район:\n");
            List<Map<String, String>> buttons = new ArrayList<>();

            for (District district : api.getDistricts()) {
                buttons.add(new HashMap<>());
                buttons.get(buttons.size()-1).put(district.getName(), district.getId().toString());
            }

            return new InlineButtonTelegramResponse(answerText.toString(), buttons);
        } else {
            TaskEntity task = result.get();
            String message = request.getText();

            switch (task.getState()) {
                case SET_DISTRICT -> {
                    Integer districtId = Integer.parseInt(message);
                    task.setDistrictId(districtId);
                    task.setState(TaskState.SET_POLYCLINIC);
                    return printPolyclinics(districtId);
                }
            }

            repository.save(task);

            return new GenericTelegramResponse("");
        }

    }

    private GenericTelegramResponse printPolyclinics(Integer distrinctId) {
        StringBuilder answerText = new StringBuilder();
        answerText.append("Выберите мед. учреждение:\n");

        for (Polyclinic polyclinic : api.getPolyclinicsByDistrict(distrinctId)) {
            answerText.append("<code>%s</code>%n".formatted(polyclinic.lpuFullName()));
        }

        return new GenericTelegramResponse(answerText.toString());
    }
}
