package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;


import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

/**
 * Interface for callback handler
 */
public interface ICallbackChainUnit {
    void setNext(ICallbackChainUnit unit);
    ICallbackChainUnit getNext();
    TelegramResponse execute(Long dialogId, CallbackData data);
}
