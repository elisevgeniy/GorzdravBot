package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramCommand;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.bot.services.*;
import ru.kusok_piroga.gorzdravbot.bot.services.PatientListCommandService;

import static ru.kusok_piroga.gorzdravbot.bot.models.Commands.*;

@TelegramController
@RequiredArgsConstructor
public class CommandController {

    private final TaskService taskService;
    private final TaskListService taskListService;
    private final StartService startService;
    private final PatientCreateCommandService patientCreateCommandService;
    private final PatientListCommandService patientListCommandService;

    @TelegramCommand(COMMAND_START)
    public TelegramResponse onStart(UpdateRequest request) {
        return startService.processCommand(request);
    }

    @TelegramCommand(COMMAND_ADD_TASK)
    public TelegramResponse onAddNewTask(UpdateRequest request) {
        return taskService.cleanStart(request);
    }

    @TelegramCommand(COMMAND_ADD_PATIENT)
    public TelegramResponse onAddNewPatient(UpdateRequest request) {
        return patientCreateCommandService.processCommand(request);
    }

    @TelegramCommand(COMMAND_LIST_PATIENT)
    public TelegramResponse onListPatient(UpdateRequest request) {
        return patientListCommandService.processCommand(request);
    }

    @TelegramCommand(COMMAND_LIST_TASK)
    public TelegramResponse onListTask(UpdateRequest request) {
        return taskListService.processCommand(request);
    }
}
