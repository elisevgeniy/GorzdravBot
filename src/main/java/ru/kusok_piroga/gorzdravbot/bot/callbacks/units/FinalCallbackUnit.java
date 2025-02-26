package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions.UnknownCallbackFunctionException;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

/**
 * Last callback handler. Only throws {@link UnknownCallbackFunctionException}
 */
public class FinalCallbackUnit extends BaseCallbackUnit {

    @Override
    public TelegramResponse execute(Long dialogId, CallbackData data) {
        throw new UnknownCallbackFunctionException();
    }
}
