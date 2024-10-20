package ru.kusok_piroga.gorzdravbot.api.models;

import java.util.List;

public record DistrictsResponse(
        List<District> result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public List<District> getDistrictList() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
