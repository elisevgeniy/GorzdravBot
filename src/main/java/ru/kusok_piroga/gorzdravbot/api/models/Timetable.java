package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record Timetable(
        List<Appointment> appointments,
        String denyCause,
        Boolean recordableDay,
        String visitStart,
        String visitEnd
) {
}
