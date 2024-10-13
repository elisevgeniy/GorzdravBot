package ru.kusok_piroga.gorzdravbot.api.models;

public record AvailableAppointment(
        String id,
        String visitStart,
        String visitEnd,
        String address,
        String number,
        String room
){}