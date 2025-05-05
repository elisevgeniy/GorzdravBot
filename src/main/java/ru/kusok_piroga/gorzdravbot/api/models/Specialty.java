package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record Specialty(
        String id,
        String ferId,
        String name,
        Integer countFreeParticipant,
        Integer countFreeTicket,
        String lastDate,
        String nearestDate,
        List<Doctor> doctors
) {
}
