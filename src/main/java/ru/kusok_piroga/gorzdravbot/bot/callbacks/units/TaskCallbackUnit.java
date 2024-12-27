package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.RestartTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
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
            default:
                return new GenericTelegramResponse("Ошибка значения callback");
        }
    }

    private boolean checkAffiliation(String function){
        switch (function){
            case FN_DELETE, FN_RESTART, FN_CANCEL:
                return true;
            default:
                return false;
        }
    }
}
