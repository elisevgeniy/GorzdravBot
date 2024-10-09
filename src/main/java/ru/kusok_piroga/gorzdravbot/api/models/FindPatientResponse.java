package ru.kusok_piroga.gorzdravbot.api.models;

public record FindPatientResponse(
        String result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public String getPatient() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
