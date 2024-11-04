package ru.kusok_piroga.gorzdravbot.recorder.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public record NotifyToChatData(long tsk, String app) {

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<NotifyToChatData> parse(String str){
        try {
            return Optional.of(new ObjectMapper().readValue(str, NotifyToChatData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
