package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskCancelCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskDeleteCommandService;
import ru.kusok_piroga.gorzdravbot.bot.responses.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

@Component
@RequiredArgsConstructor
public class TaskCallbackUnit extends BaseCallbackUnit {

    private final TaskDeleteCommandService taskDeleteCommandService;
    private final TaskCancelCommandService taskCancelCommandService;

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
                return null;
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
