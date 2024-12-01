package ru.kusok_piroga.gorzdravbot.recorder.utils;

import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@UtilityClass
public class ConstraintChecker {
    public static boolean check(TaskEntity task, AvailableAppointment appointment){
        return checkLimitConstraint(
                appointment.visitStart(),
                task.getTimeLimits(),
                task.getHighDateLimit()
        );
    }

    private static boolean checkLimitConstraint(String visitStart, TaskTimeLimits timeLimits, Date highDateLimit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime visitStartDateTime = LocalDateTime.parse(visitStart, formatter);
            LocalDate highDateLimitPlusDay = highDateLimit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            return timeLimits.validateTime(visitStartDateTime.toLocalTime()) &&
                   visitStartDateTime.toLocalDate().isBefore(highDateLimitPlusDay);

        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
