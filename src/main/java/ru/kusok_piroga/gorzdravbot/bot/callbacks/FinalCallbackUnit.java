package ru.kusok_piroga.gorzdravbot.bot.callbacks;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

public class FinalCallbackUnit extends BaseCallbackUnit {

    @Override
    public TelegramResponse execute(CallbackData data) {
        return new GenericTelegramResponse("Неизвестная команда");
    }
}
