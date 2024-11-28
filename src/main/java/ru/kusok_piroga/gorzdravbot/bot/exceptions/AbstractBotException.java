package ru.kusok_piroga.gorzdravbot.bot.exceptions;

public abstract class AbstractBotException extends RuntimeException {
    @Override
    public abstract String getMessage();
}
