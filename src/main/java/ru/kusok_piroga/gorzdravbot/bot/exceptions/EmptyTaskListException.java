package ru.kusok_piroga.gorzdravbot.bot.exceptions;

import ru.kusok_piroga.gorzdravbot.bot.models.Commands;

public class EmptyTaskListException extends AbstractBotException {
    public static final String MESSAGE = "Задачи не найдены. Добавить задачу можно с помощью " + Commands.COMMAND_ADD_TASK;

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
