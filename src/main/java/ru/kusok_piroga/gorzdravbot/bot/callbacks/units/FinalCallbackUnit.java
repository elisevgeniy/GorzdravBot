package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions.UnknownCallbackFunctionException;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

public class FinalCallbackUnit extends BaseCallbackUnit {

    @Override
    public TelegramResponse execute(CallbackData data) {
        throw new UnknownCallbackFunctionException();
    }
}
