package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record TimetablesResponse(
        List<Timetable> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<Timetable> getTimetables() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
