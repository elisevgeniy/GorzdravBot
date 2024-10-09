package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record AppointmentsResponse(
        List<Appointment> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<Appointment> getAppointments() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
