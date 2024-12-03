package ru.kusok_piroga.gorzdravbot.domain.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DateLimitParseException extends Throwable {
    public DateLimitParseException(String message) {
        super(message);
    }
}
