package ru.kusok_piroga.gorzdravbot.producer.utils;

import org.junit.jupiter.api.Test;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskValidatorTest {

    @Test
    void validateTime() {
        assertTrue(TaskValidator.validateTime("10:00"));
        assertTrue(TaskValidator.validateTime("00:59"));
        assertTrue(TaskValidator.validateTime("23:59"));
        assertTrue(TaskValidator.validateTime("00:00"));

        assertFalse(TaskValidator.validateTime("24:00"));
        assertFalse(TaskValidator.validateTime("23:60"));
        assertFalse(TaskValidator.validateTime("99:99"));
    }

    @Test
    void validateTaskTimeLimits() {
        TaskEntity task = new TaskEntity();
        task.setLowTimeLimit("10:00");
        task.setHighTimeLimit("12:00");
        assertTrue(TaskValidator.validateTaskTimeLimits(task));

        task.setLowTimeLimit("12:00");
        task.setHighTimeLimit("12:30");
        assertTrue(TaskValidator.validateTaskTimeLimits(task));

        task.setLowTimeLimit("12:30");
        task.setHighTimeLimit("12:30");
        assertTrue(TaskValidator.validateTaskTimeLimits(task));

        task.setLowTimeLimit("12:00");
        task.setHighTimeLimit("10:00");
        assertFalse(TaskValidator.validateTaskTimeLimits(task));

        task.setLowTimeLimit("12:30");
        task.setHighTimeLimit("12:00");
        assertFalse(TaskValidator.validateTaskTimeLimits(task));
    }
}