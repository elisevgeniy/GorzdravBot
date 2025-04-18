package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.AccessDeniedException;
import ru.kusok_piroga.gorzdravbot.bot.models.dto.TaskChangeDateDto;
import ru.kusok_piroga.gorzdravbot.bot.models.dto.TaskChangeTimeDto;
import ru.kusok_piroga.gorzdravbot.bot.models.dto.TaskFastRecordDto;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

@Service
@RequiredArgsConstructor
public class TaskChangeCommandService implements ICommandService {

    private final TaskService taskService;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return null;
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }

    public TelegramResponse changeTime(TaskChangeTimeDto taskChangeTimeDto){
        if (!taskService.validateTaskIdByDialogId(
                taskChangeTimeDto.taskId(),
                taskChangeTimeDto.dialogId()
        )) {
            throw new AccessDeniedException();
        }

        if (taskService.changeTime(taskChangeTimeDto.taskId(), taskChangeTimeDto.timeLimits())){
            return new GenericTelegramResponse("Ограничение изменено");
        } else {
            return new GenericTelegramResponse("Ограничение не изменено!");
        }
    }

    public TelegramResponse changeDate(TaskChangeDateDto taskChangeDateDto){
        if (!taskService.validateTaskIdByDialogId(
                taskChangeDateDto.taskId(),
                taskChangeDateDto.dialogId()
        )) {
            throw new AccessDeniedException();
        }

        if (taskService.changeDate(taskChangeDateDto.taskId(), taskChangeDateDto.dateLimits())){
            return new GenericTelegramResponse("Ограничение изменено");
        } else {
            return new GenericTelegramResponse("Ограничение не изменено!");
        }
    }

    /**
     * <p>
     *     Switch task notification status.
     *     Fast recording means that notification has already been sent and next appointments check will make record
     * </p>
     * @param taskFastRecordDto
     * @return telegram message contains current Fast Record status
     */
    public TelegramResponse changeFastRecord(TaskFastRecordDto taskFastRecordDto) {
        if (!taskService.validateTaskIdByDialogId(
                taskFastRecordDto.taskId(),
                taskFastRecordDto.dialogId()
        )) {
            throw new AccessDeniedException();
        }

        if (taskService.switchFastRecord(taskFastRecordDto.taskId())){
            return new GenericTelegramResponse("При появлении номерка бот сразу Вас запишет");
        } else {
            return new GenericTelegramResponse("При появлении номерка бот направит Вам уведомление");
        }
    }
}
