package ru.kusok_piroga.gorzdravbot.bot.callbacks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * DTO for {@link ru.kusok_piroga.gorzdravbot.bot.callbacks.units.SkipAppointmentCallbackUnit}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SkipAppointmentDto(Long taskId, String appointmentId) {
    public static Optional<SkipAppointmentDto> parse(String data){
        try {
            return Optional.of(new ObjectMapper().readValue(data, SkipAppointmentDto.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}