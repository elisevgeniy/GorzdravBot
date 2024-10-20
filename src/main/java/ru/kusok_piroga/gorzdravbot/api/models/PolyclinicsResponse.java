package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record PolyclinicsResponse(
        List<Polyclinic> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<Polyclinic> getPolyclinicList() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
