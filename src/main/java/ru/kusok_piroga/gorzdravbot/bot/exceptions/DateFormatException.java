package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class DateFormatException extends AbstractBotException {
    public static final String MESSAGE = "Ошибка формата даты. Попробуйте ещё";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
