package ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions;

import ru.kusok_piroga.gorzdravbot.bot.exceptions.AbstractBotException;

public class WrongCallbackDataException extends AbstractBotException {
    public static final String MESSAGE = "Ошибка значения callback";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
