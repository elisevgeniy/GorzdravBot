package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class CreateTaskException extends AbstractBotException {
    public static final String MESSAGE = "Ошибка при создании задачи. Попробуйте ещё раз";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
