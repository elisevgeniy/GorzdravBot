package ru.kusok_piroga.gorzdravbot.api.models;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseResponse {
    private final Boolean success;
    private final Integer errorCode;
    private final String message;
    private final String stackTrace;

    public boolean isSuccess() {
        return success;
    }
}
