package ru.kusok_piroga.gorzdravbot.api.models;

public record FindPatientResponse(
        String result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public String getPatientId() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
