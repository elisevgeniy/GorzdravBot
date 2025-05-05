package ru.kusok_piroga.gorzdravbot.api.models;

public record PolyclinicResponse(
        Polyclinic result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public Polyclinic getPolyclinic() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
