package ru.kusok_piroga.gorzdravbot.api.models;

public record Polyclinic (
    Integer id,
    String description,
    Integer district,
    Integer districtId,
    String districtName,
    Boolean isActive,
    String lpuFullName,
    String lpuShortName,
    String lpuType,
    String oid,
    Integer partOf,
    String headOrganization,
    String organization,
    String address,
    String phone,
    String email,
    String longitude,
    String latitude,
    Boolean covidVaccination,
    Boolean inDepthExamination,
    String covidVaccinationTicketCount,
    String covidVaccinationTicketReceiveTime
){}
