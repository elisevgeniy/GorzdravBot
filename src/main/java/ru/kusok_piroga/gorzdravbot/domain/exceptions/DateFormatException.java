package ru.kusok_piroga.gorzdravbot.domain.exceptions;

public class DateFormatException extends AbstractBotException {
    public static final String message = "Ошибка формата даты. Попробуйте ещё";

    @Override
    public String getMessage() {
        return message;
    }
}
