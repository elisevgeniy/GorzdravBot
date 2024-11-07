package ru.kusok_piroga.gorzdravbot.callbacks;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskCancelService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskDeleteService;
import ru.kusok_piroga.gorzdravbot.common.responses.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.callbacks.models.CallbackData;

@Component
@RequiredArgsConstructor
public class TaskCallbackChain extends BaseCallbackChain {

    private final TaskDeleteService taskDeleteService;
    private final TaskCancelService taskCancelService;

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
                taskDeleteService.deleteTask(data.d());
                return new DeleteMessageTelegramResponse();
            case FN_CANCEL:
                return taskCancelService.cancelTask(data.d());
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
