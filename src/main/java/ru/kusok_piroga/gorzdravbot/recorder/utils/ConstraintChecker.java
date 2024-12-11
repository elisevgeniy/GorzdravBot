package ru.kusok_piroga.gorzdravbot.recorder.utils;

import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.SkipAppointmentEntity;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class ConstraintChecker {
    public static boolean check(TaskEntity task, AvailableAppointment appointment) {
        return checkLimitConstraint(
                appointment.visitStart(),
                task.getTimeLimits(),
                task.getDateLimits()
        ) &&
               checkIsNotSkipped(task, appointment);
    }

    private static boolean checkLimitConstraint(String visitStart, TaskTimeLimits timeLimits, TaskDateLimits dateLimits) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime visitStartDateTime = LocalDateTime.parse(visitStart, formatter);

            return timeLimits.validateTime(visitStartDateTime.toLocalTime()) &&
                   dateLimits.validateDate(visitStartDateTime.toLocalDate());

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean checkIsNotSkipped(TaskEntity task, AvailableAppointment appointment) {
        SkipAppointmentEntity toCheck = new SkipAppointmentEntity();
        toCheck.setTask(task);
        toCheck.setAppointmentId(appointment.id());
        return !task.getSkippedAppointments().contains(toCheck);
    }
}
