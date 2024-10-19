package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.*;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.PatientService;
import ru.kusok_piroga.gorzdravbot.bot.services.StartService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskService;

import static ru.kusok_piroga.gorzdravbot.bot.models.Commands.*;

@TelegramController
@RequiredArgsConstructor
public class MainController {

    private final ApiService api;
    private final LastCommandService lastCommandService;
    private final TaskService taskService;
    private final StartService startService;
    private final PatientService patientService;

    @TelegramCommand(COMMAND_START)
    public TelegramResponse onStart(UpdateRequest request) {
        return startService.execute(request);
    }

    @TelegramCommand(COMMAND_ADD_TASK)
    public TelegramResponse onAddNewTask(UpdateRequest request) {
        return taskService.cleanStart(request);
    }

    @TelegramCommand(COMMAND_ADD_PATIENT)
    public TelegramResponse onAddNewPatient(UpdateRequest request) {
        return patientService.cleanStart(request);
    }

    @TelegramRequest(requestType = {RequestType.MESSAGE, RequestType.CALLBACK_QUERY})
    public TelegramResponse onAnyString(UpdateRequest request){
        return lastCommandService.getLastCommandService(request.getChatId())
                .execute(request);
    }
}
