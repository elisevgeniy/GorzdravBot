package ru.kusok_piroga.gorzdravbot.domain.exceptions;

public abstract class AbstractBotException extends RuntimeException {
    public abstract String getMessage();
}
