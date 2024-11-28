package ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions;

import ru.kusok_piroga.gorzdravbot.bot.exceptions.AbstractBotException;

public class UnknownCallbackFunctionException extends AbstractBotException {
    public static final String MESSAGE = "Нет обработчика для callback кнопки";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
