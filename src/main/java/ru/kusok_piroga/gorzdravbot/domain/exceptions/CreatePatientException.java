package ru.kusok_piroga.gorzdravbot.domain.exceptions;

public class CreatePatientException extends AbstractBotException {
    public static final String message = "Ошибка при создании пациента";

    @Override
    public String getMessage() {
        return message;
    }
}
