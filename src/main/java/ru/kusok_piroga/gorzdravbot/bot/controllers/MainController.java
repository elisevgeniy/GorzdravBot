package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.*;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.bot.services.*;

import static ru.kusok_piroga.gorzdravbot.bot.models.Commands.*;

@TelegramController
@RequiredArgsConstructor
public class MainController {

    private final LastCommandService lastCommandService;
    private final TaskService taskService;
    private final TaskListService taskListService;
    private final TaskDeleteService taskDeleteService;
    private final StartService startService;
    private final PatientCreateService patientCreateService;
    private final PatientDeleteService patientDeleteService;
    private final PatientListService patientListService;

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
        return patientCreateService.cleanStart(request);
    }

    @TelegramCommand(COMMAND_LIST_PATIENT)
    public TelegramResponse onListPatient(UpdateRequest request) {
        return patientListService.execute(request);
    }

    @TelegramCommand(COMMAND_LIST_TASK)
    public TelegramResponse onListTask(UpdateRequest request) {
        return taskListService.execute(request);
    }

    @TelegramRequest(COMMAND_DELETE_TASK + "/{taskId}")
    public void onListTask(@TelegramPatternVariable("taskId") String taskId, UpdateRequest request) {
        taskDeleteService.deleteTask(taskId, request);
    }

    @TelegramCommand(COMMAND_DELETE_PATIENT)
    public TelegramResponse onDeletePatient(UpdateRequest request) {
        return patientDeleteService.execute(request);
    }

    @TelegramRequest(requestType = {RequestType.MESSAGE, RequestType.CALLBACK_QUERY})
    public TelegramResponse onAnyString(UpdateRequest request){
        return lastCommandService.getLastCommandService(request.getChatId())
                .execute(request);
    }
}
