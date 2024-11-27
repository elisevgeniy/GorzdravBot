package ru.kusok_piroga.gorzdravbot.recorder.utils;

import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.kusok_piroga.gorzdravbot.utils.DateConverter.*;

@UtilityClass
public class ConstraintChecker {
    public static boolean check(TaskEntity task, AvailableAppointment appointment){
        return checkLimitConstraint(
                appointment.visitStart(),
                task.getLowTimeLimit(),
                task.getHighTimeLimit(),
                task.getHighDateLimit()
        );
    }

    private static boolean checkLimitConstraint(String visitStart, String lowTimeLimitStr, String highTimeLimitStr, Date highDateLimit) {
        DateFormat limitFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date visitStartTime = parseAppointmentDate(visitStart);
            Date lowTimeLimit = minusTenMin(limitFormatter.parse(visitStart.substring(0, 11) + lowTimeLimitStr));
            Date highTimeLimit = plusTenMin(limitFormatter.parse(visitStart.substring(0, 11) + highTimeLimitStr));
            Date highDateLimitPlusDay = plusOneDay(highDateLimit);

            return lowTimeLimit.before(visitStartTime) && visitStartTime.before(highTimeLimit) && visitStartTime.before(highDateLimitPlusDay);

        } catch (ParseException e) {
            return false;
        }
    }
}
