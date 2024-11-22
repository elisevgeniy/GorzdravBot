package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class TimeFormatException extends AbstractBotException {
    public static final String MESSAGE = "Ошибка формата времени. Попробуйте ещё";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
