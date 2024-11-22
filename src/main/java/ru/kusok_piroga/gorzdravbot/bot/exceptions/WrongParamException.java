package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class WrongParamException extends AbstractBotException {
    public static final String MESSAGE = "Передан неверный параметр, попробуйте ещё";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
