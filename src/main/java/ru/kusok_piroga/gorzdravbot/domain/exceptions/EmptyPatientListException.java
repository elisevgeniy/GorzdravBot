package ru.kusok_piroga.gorzdravbot.domain.exceptions;

import ru.kusok_piroga.gorzdravbot.bot.models.Commands;

public class EmptyPatientListException extends AbstractBotException {
    public static final String message = "Пациенты не найдены. Добавить пациента можно с помощью " + Commands.COMMAND_ADD_PATIENT;

    @Override
    public String getMessage() {
        return message;
    }
}
