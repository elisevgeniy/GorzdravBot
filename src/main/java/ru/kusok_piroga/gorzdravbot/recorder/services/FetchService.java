package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static ru.kusok_piroga.gorzdravbot.common.DateConverter.*;

@Service
@RequiredArgsConstructor
public class FetchService {
    private final ApiService api;

    public List<AvailableAppointment> getValidAvailableAppointments(TaskEntity task){
        List<AvailableAppointment> availableAppointments = api.getAvailableAppointments(task.getPolyclinicId(), task.getDoctorId());

        if (availableAppointments.isEmpty()) {
            return emptyList();
        }

        return availableAppointments.stream().filter(
                availableAppointment -> checkLimitConstraint(availableAppointment.visitStart(), task.getLowTimeLimit(), task.getHighTimeLimit())
        ).toList();
    }
    private boolean checkLimitConstraint(String visitStart, String lowTimeLimitStr, String highTimeLimitStr) {
        DateFormat limitFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date visitStartTime = parseAppointmentDate(visitStart);
            Date lowTimeLimit = minusTenMin(limitFormatter.parse(visitStart.substring(0, 11) + lowTimeLimitStr));
            Date highTimeLimit = plusTenMin(limitFormatter.parse(visitStart.substring(0, 11) + highTimeLimitStr));

            return lowTimeLimit.before(visitStartTime) && visitStartTime.before(highTimeLimit);

        } catch (ParseException e) {
            return false;
        }
    }
}
