package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.handler.UpdateHandlerProperties;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.ChangeTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.RestartTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.bot.responses.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskCancelCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskDeleteCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskRestartCommandService;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCallbackUnit extends BaseCallbackUnit {

    private final TaskDeleteCommandService taskDeleteCommandService;
    private final TaskCancelCommandService taskCancelCommandService;
    private final TaskRestartCommandService taskRestartCommandService;

    public static final String FN_DELETE = "tsk_del";
    public static final String FN_RESTART = "tsk_up";
    public static final String FN_CANCEL = "tsk_cnl";
    public static final String FN_CHANGE = "tsk_cng";

    @Override
    public TelegramResponse execute(CallbackData data) {
        if (!checkAffiliation(data.fn())){
            return getNext().execute(data);
        }

        switch (data.fn()){
            case FN_DELETE:
                taskDeleteCommandService.deleteTask(data.d());
                return new DeleteMessageTelegramResponse();
            case FN_CANCEL:
                return taskCancelCommandService.cancelTask(data.d());
            case FN_RESTART:
                Optional<RestartTaskDto> restartTaskDto = RestartTaskDto.parse(data.d());
                if (restartTaskDto.isEmpty()) {
                    log.error("Callback wrong format");
                    return new GenericTelegramResponse("Ошибка значения callback");
                }
                return taskRestartCommandService.restartTask(restartTaskDto.get().taskId());
            case FN_CHANGE:
                Optional<ChangeTaskDto> changeTaskDto = ChangeTaskDto.parse(data.d());
                if (changeTaskDto.isEmpty()) {
                    log.error("Callback wrong format");
                    return new GenericTelegramResponse("Ошибка значения callback");
                }
                GenericTelegramResponse response = new GenericTelegramResponse("""
                        *Время*
                        Для изменения времени введите следующую команду и желаемое ограничение по времени (только команда без времени - снять ограничение)
                        `%s/%d/time` _желаемое время_
                        ||Формат ограничения следующий (можно 1 и 2 вместе)):
                        1. Диапазон времени, в котором искать номерки в формате
                        чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию 00:00 - 23:59)
                        2. Диапазон времени, в котором НЕ искать номерки в формате
                        !чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию не будет установлен)||
                        
                        *Дата*
                        Для изменения даты введите следующую команду и желаемое ограничение по дате
                        `%s/%d/date` _желаемая дата_
                        ||Формат ограничения следующий (либо 1, либо 2 и 3 (2 и 3 можно объединить)):
                        1. Дата до которой искать номерки в формате
                        дд.мм.гггг
                        2. Диапазон дат, в котором искать номерки в формате
                        дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг
                        3. Диапазон дат, в котором искать и НЕ номерки в формате
                        !дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг;||
                        """.formatted(
                        Commands.COMMAND_CHANGE_TASK,
                        changeTaskDto.get().taskId(),
                        Commands.COMMAND_CHANGE_TASK,
                        changeTaskDto.get().taskId()
                        ));
                response.setParseMode(UpdateHandlerProperties.ParseMode.MARKDOWN);
                return response;
            default:
                return new GenericTelegramResponse("Ошибка значения callback");
        }
    }

    private boolean checkAffiliation(String function){
        return switch (function) {
            case FN_DELETE, FN_RESTART, FN_CANCEL, FN_CHANGE -> true;
            default -> false;
        };
    }
}
