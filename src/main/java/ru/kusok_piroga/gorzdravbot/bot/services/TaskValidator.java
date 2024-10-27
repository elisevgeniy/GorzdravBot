package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;

@UtilityClass
public class TaskValidator {
    public static boolean validateTime(String time) {
        return time.matches("\\d\\d:\\d\\d")
                && Integer.parseInt(time.substring(0, 2)) >= 0
                && Integer.parseInt(time.substring(0, 2)) <= 23
                && Integer.parseInt(time.substring(3)) >= 0
                && Integer.parseInt(time.substring(3)) <= 59;
    }

    public static boolean validateTaskTimeLimits(TaskEntity task) {
        int lowH = Integer.parseInt(task.getLowTimeLimit().substring(0, 2));
        int lowM = Integer.parseInt(task.getLowTimeLimit().substring(3));
        int highH = Integer.parseInt(task.getHighTimeLimit().substring(0, 2));
        int highM = Integer.parseInt(task.getHighTimeLimit().substring(3));
        return lowH < highH || lowH == highH && lowM <= highM;
    }
}
