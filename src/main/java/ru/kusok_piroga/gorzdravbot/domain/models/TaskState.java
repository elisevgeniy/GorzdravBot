package ru.kusok_piroga.gorzdravbot.domain.models;

public enum TaskState {
    INIT,
    SET_PATIENT,
    SET_DISTRICT,
    SET_POLYCLINIC,
    SET_SPECIALITY,
    SET_DOCTOR,
    SET_TIME_LOW_LIMITS,
    SET_TIME_HIGH_LIMITS,
    SET_DATE_LIMITS,
    SETUPED
}
