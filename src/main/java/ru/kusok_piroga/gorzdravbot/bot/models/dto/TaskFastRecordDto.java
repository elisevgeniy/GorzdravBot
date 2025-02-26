package ru.kusok_piroga.gorzdravbot.bot.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * dto for {@link ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskFastRecordDto(Long dialogId, Long taskId) implements Serializable {
}