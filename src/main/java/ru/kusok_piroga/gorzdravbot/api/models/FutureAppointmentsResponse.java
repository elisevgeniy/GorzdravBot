package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record FutureAppointmentsResponse(
        List<FutureAppointment> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<FutureAppointment> getAppointments() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
