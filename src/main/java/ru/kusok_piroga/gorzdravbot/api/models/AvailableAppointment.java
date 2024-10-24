package ru.kusok_piroga.gorzdravbot.api.models;

public record AvailableAppointment(
        String id,
        String visitStart,
        String visitEnd,
        String address,
        String number,
        String room
){
    public AvailableAppointment(String id, String visitStart, String visitEnd, String address, String number, String room) {
        this.id = id;
        this.visitStart = visitStart.replace('T', ' ');
        this.visitEnd = visitEnd.replace('T', ' ');
        this.address = address;
        this.number = number;
        this.room = room;
    }
}