package ru.kusok_piroga.gorzdravbot.bot.callbacks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * DTO for change {@link ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FastRecordTaskDto(Long taskId){
    public static Optional<FastRecordTaskDto> parse(String data){
        try {
            return Optional.of(new ObjectMapper().readValue(data, FastRecordTaskDto.class));
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