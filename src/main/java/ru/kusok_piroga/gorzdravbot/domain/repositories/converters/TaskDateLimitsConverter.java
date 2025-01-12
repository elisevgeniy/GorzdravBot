package ru.kusok_piroga.gorzdravbot.domain.repositories.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;

@Converter
@Slf4j
public class TaskDateLimitsConverter implements AttributeConverter<TaskDateLimits, String> {

    @Override
    public String convertToDatabaseColumn(TaskDateLimits attribute) {
        return (attribute == null) ? "" : attribute.toString();
    }

    @Override
    public TaskDateLimits convertToEntityAttribute(String dbData) {
        try {
            return new TaskDateLimits(dbData);
        } catch (DateLimitParseException e) {
            log.error(e.toString());
            return null;
        }
    }
}
