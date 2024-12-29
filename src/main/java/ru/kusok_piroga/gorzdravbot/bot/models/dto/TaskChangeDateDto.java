package ru.kusok_piroga.gorzdravbot.bot.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;

import java.io.Serializable;

/**
 * dto for {@link ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskChangeDateDto(Long dialogId, Long taskId, TaskDateLimits dateLimits) implements Serializable {
}