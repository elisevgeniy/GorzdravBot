package ru.kusok_piroga.gorzdravbot.domain.repositories.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

@Converter
public class TaskTimeLimitsConverter implements AttributeConverter<TaskTimeLimits, String> {

    @Override
    public String convertToDatabaseColumn(TaskTimeLimits attribute) {
        return attribute.toString();
    }

    @Override
    public TaskTimeLimits convertToEntityAttribute(String dbData) {
        try {
            return new TaskTimeLimits(dbData);
        } catch (TimeLimitParseException e) {
            return null;
        }
    }
}
