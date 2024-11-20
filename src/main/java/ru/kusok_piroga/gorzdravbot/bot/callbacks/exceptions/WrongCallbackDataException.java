package ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions;

import ru.kusok_piroga.gorzdravbot.domain.exceptions.AbstractBotException;

public class WrongCallbackDataException extends AbstractBotException {
    public static final String message = "Ошибка значения callback";

    @Override
    public String getMessage() {
        return message;
    }
}
