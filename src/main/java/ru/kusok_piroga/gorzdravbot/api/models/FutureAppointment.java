package ru.kusok_piroga.gorzdravbot.api.models;

public record FutureAppointment(
        String appointmentId,
        String dateCreatedAppointment,
        String doctorBringReferal,
        Doctor doctorRendingConsultation,
        Boolean isAppointmentByReferral,
        String lpuAddress,
        String lpuFullName,
        String lpuId,
        String lpuPhone,
        String lpuShortName,
        String patientId,
        String referralId,
        String specialityBringReferal,
        Specialty specialityRendingConsultation,
        String visitStart,
        String status,
        String type,
        String positionBringReferal,
        String positionRendingConsultation,
        String infections,
        String patientFullName,
        String patientBirthDate
) {
}

