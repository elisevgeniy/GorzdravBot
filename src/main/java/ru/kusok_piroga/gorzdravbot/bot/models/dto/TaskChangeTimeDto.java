package ru.kusok_piroga.gorzdravbot.bot.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import java.io.Serializable;

/**
 * dto for {@link ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskChangeTimeDto(Long dialogId, Long taskId, TaskTimeLimits timeLimits) implements Serializable {
}