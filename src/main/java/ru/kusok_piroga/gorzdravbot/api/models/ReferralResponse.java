package ru.kusok_piroga.gorzdravbot.api.models;

public record ReferralResponse(
        ReferralInfo result,
        Boolean success,
        Integer errorCode,
        String message,
        String stackTrace
) {
    public ReferralInfo getReferralInfo() {
        return result;
    }
    public boolean isSuccess() {
        return success;
    }
}
