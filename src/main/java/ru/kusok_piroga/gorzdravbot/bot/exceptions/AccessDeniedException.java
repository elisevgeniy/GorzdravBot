package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class AccessDeniedException extends AbstractBotException {
    public static final String MESSAGE = "Доступ запрещён";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
