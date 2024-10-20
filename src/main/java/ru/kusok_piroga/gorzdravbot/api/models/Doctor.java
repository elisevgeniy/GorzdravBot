package ru.kusok_piroga.gorzdravbot.api.models;

public record Doctor(
        String id,
        String ariaNumber,
        String ariaType,
        String comment,
        Integer freeParticipantCount,
        Integer freeTicketCount,
        String ferId,
        String name,
        String lastDate,
        String nearestDate
) {
}
