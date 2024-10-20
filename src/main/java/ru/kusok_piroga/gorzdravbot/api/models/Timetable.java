package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record Timetable(
        List<AvailableAppointment> availableAppointments,
        String denyCause,
        Boolean recordableDay,
        String visitStart,
        String visitEnd
) {
}
