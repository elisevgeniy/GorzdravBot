package ru.kusok_piroga.gorzdravbot.callbacks;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.callbacks.models.CallbackData;

public class FinalCallbackChain extends BaseCallbackChain {

    @Override
    public TelegramResponse execute(CallbackData data) {
        return new GenericTelegramResponse("Неизвестная команда");
    }
}
