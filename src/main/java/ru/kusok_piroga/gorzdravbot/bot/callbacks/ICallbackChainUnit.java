package ru.kusok_piroga.gorzdravbot.bot.callbacks;


import io.github.drednote.telegram.response.TelegramResponse;
import ru.kusok_piroga.gorzdravbot.common.models.CallbackData;

public interface ICallbackChainUnit {
    void setNext(ICallbackChainUnit unit);
    ICallbackChainUnit getNext();
    TelegramResponse execute(CallbackData data);
}