package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class CreatePatientException extends AbstractBotException {
    public static final String MESSAGE = "Ошибка при создании пациента";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
