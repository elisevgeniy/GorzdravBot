package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public class RawSendException extends AbstractBotException {
    public static final String MESSAGE = "Не удалось отправить Вам сообщение";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
