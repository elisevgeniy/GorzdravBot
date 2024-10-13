package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record AvailableAppointmentsResponse(
        List<AvailableAppointment> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<AvailableAppointment> getAppointments() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
