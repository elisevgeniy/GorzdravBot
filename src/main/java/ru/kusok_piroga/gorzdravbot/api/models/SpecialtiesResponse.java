package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record SpecialtiesResponse(
        List<Specialty> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<Specialty> getSpecialties() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
