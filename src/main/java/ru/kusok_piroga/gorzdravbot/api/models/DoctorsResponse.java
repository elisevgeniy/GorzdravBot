package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record DoctorsResponse(
        List<Doctor> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<Doctor> getDoctors() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
