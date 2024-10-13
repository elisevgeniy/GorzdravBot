package ru.kusok_piroga.gorzdravbot.api.models;

public record AppointmentActionRequestBody(
        Integer lpuId,
        String patientId,
        String appointmentId
) {
}
