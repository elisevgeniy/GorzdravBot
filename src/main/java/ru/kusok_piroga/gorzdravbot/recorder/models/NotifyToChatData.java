package ru.kusok_piroga.gorzdravbot.recorder.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotifyToChatData {

    private final ObjectMapper mapepr = new ObjectMapper();
    private final long taskId;
    private final String appointmentId;

    @Override
    public String toString() {
        try {
            return mapepr.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
