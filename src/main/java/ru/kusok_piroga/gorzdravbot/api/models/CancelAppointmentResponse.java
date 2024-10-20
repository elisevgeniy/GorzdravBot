package ru.kusok_piroga.gorzdravbot.api.models;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelAppointmentResponse {
    private final Boolean result;
    private final Boolean success;
    private final Integer errorCode;
    private final String message;
    private final String stackTrace;

    public boolean isCanceled() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
