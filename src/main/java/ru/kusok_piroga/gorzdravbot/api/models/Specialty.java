package ru.kusok_piroga.gorzdravbot.api.models;

public record Specialty(
        String id,
        String ferId,
        String name,
        Integer countFreeParticipant,
        Integer countFreeTicket,
        String lastDate,
        String nearestDate
) {
}
