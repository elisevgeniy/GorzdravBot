package ru.kusok_piroga.gorzdravbot.bot.callbacks;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskDeleteService;
import ru.kusok_piroga.gorzdravbot.common.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.common.models.CallbackData;

@Component
@RequiredArgsConstructor
public class TaskCallbackChain extends BaseCallbackChain {

    private final TaskDeleteService taskDeleteService;

    public static final String FN_DELETE = "tsk_del";
    public static final String FN_RESTART = "tsk_up";

    @Override
    public TelegramResponse execute(CallbackData data) {
        if (!checkAffiliation(data.fn())){
            return getNext().execute(data);
        }

        switch (data.fn()){
            case FN_DELETE:
                taskDeleteService.deleteTask(data.d());
                return new DeleteMessageTelegramResponse();
            case FN_RESTART:
                return null;
            default:
                return new GenericTelegramResponse("Ошибка значения callback");
        }
    }

    private boolean checkAffiliation(String function){
        switch (function){
            case FN_DELETE, FN_RESTART:
                return true;
            default:
                return false;
        }
    }
}
