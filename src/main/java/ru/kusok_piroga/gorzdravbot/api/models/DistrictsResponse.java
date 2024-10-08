package ru.kusok_piroga.gorzdravbot.api.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class DistrictsResponse {
    private List<District> result;
    @Getter
    private boolean success;
    private int errorCode;
    private String message;
    private String stackTrace;

    public List<District> getDistrictList() {
        return result;
    }
}
