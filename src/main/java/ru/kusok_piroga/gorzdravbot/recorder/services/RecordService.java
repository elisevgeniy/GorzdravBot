package ru.kusok_piroga.gorzdravbot.recorder.services;

import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RecordService {

    public static final int DELAY_FOR_RECORD_VALUE = 5;
    public static final int DELAY_FOR_RECORD_UNIT = Calendar.MINUTE;

    public boolean isTimeToRecord(TaskEntity task) {
        if (task.getLastNotify() == null) {
            return false;
        }

        Calendar notifyLowLimitCal = Calendar.getInstance();
        notifyLowLimitCal.setTime(new Date());
        notifyLowLimitCal.add(DELAY_FOR_RECORD_UNIT, -1 * DELAY_FOR_RECORD_VALUE);

        Date notifyLowLimit = notifyLowLimitCal.getTime();

        return task.getLastNotify().before(notifyLowLimit);
    }

    public boolean makeRecord(TaskEntity task, List<AvailableAppointment> availableAppointments){
        if (Boolean.TRUE.equals(task.getCompleted())){
            return false;
        }

        // code

        return false;
    }
}
