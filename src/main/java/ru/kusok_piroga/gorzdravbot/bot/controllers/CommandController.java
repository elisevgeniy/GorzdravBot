package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramCommand;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramPatternVariable;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.WrongParamException;
import ru.kusok_piroga.gorzdravbot.bot.models.dto.TaskChangeDateDto;
import ru.kusok_piroga.gorzdravbot.bot.models.dto.TaskChangeTimeDto;
import ru.kusok_piroga.gorzdravbot.bot.services.*;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import static ru.kusok_piroga.gorzdravbot.bot.models.Commands.*;

@TelegramController
@RequiredArgsConstructor
public class CommandController {

    private final TaskCreateCommandService taskCreateCommandService;
    private final ReferralCreateCommandService referralCreateCommandService;
    private final TaskChangeCommandService taskChangeCommandService;
    private final TaskListCommandService taskListCommandService;
    private final StartService startService;
    private final PatientCreateCommandService patientCreateCommandService;
    private final PatientListCommandService patientListCommandService;

    @TelegramCommand(COMMAND_START)
    public TelegramResponse onStart(UpdateRequest request) {
        return startService.processCommand(request);
    }

    @TelegramCommand(COMMAND_ADD_TASK)
    public TelegramResponse onAddNewTask(UpdateRequest request) {
        return taskCreateCommandService.processCommand(request);
    }

    @TelegramCommand(COMMAND_ADD_REFERRAL)
    public TelegramResponse onAddNewTaskByReferral(UpdateRequest request) {
        return referralCreateCommandService.processCommand(request);
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
        return taskListCommandService.processCommand(request);
    }

    @TelegramRequest(COMMAND_CHANGE_TASK + "/{taskId}/time {value}")
    public TelegramResponse onChangeTaskTime(@TelegramPatternVariable("taskId") String taskIdStr, @TelegramPatternVariable("value") String newTimeLimitsStr, UpdateRequest request) {
        if (taskIdStr.isEmpty() || taskIdStr.isBlank() ||
            newTimeLimitsStr.isEmpty() || newTimeLimitsStr.isBlank()){
            throw new WrongParamException();
        }

        try {
            return taskChangeCommandService.changeTime(new TaskChangeTimeDto(
                    request.getChatId(),
                    Long.parseLong(taskIdStr),
                    new TaskTimeLimits(newTimeLimitsStr)
            ));
        } catch (TimeLimitParseException | NumberFormatException e) {
            throw new WrongParamException();
        }
    }

    @TelegramRequest(COMMAND_CHANGE_TASK + "/{taskId}/time")
    public TelegramResponse onChangeTaskTimeEmpty(@TelegramPatternVariable("taskId") String taskIdStr, UpdateRequest request) {
        if (taskIdStr.isEmpty() || taskIdStr.isBlank()){
            throw new WrongParamException();
        }

        try {
            return taskChangeCommandService.changeTime(new TaskChangeTimeDto(
                    request.getChatId(),
                    Long.parseLong(taskIdStr),
                    new TaskTimeLimits("")
            ));
        } catch (TimeLimitParseException | NumberFormatException e) {
            throw new WrongParamException();
        }
    }

    @TelegramRequest(COMMAND_CHANGE_TASK + "/{taskId}/date {value}")
    public TelegramResponse onChangeTaskDate(@TelegramPatternVariable("taskId") String taskIdStr, @TelegramPatternVariable("value") String newDateLimitsStr, UpdateRequest request) {
        if (taskIdStr.isEmpty() || taskIdStr.isBlank() ||
            newDateLimitsStr.isEmpty() || newDateLimitsStr.isBlank()){
            throw new WrongParamException();
        }

        try {
            return taskChangeCommandService.changeDate(new TaskChangeDateDto(
                    request.getChatId(),
                    Long.parseLong(taskIdStr),
                    new TaskDateLimits(newDateLimitsStr)
            ));
        } catch (NumberFormatException | DateLimitParseException e) {
            throw new WrongParamException();
        }
    }
}
