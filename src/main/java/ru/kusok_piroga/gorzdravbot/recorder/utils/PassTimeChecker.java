package ru.kusok_piroga.gorzdravbot.recorder.utils;

import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class PassTimeChecker {
    /**
     * Checks that the date limit has not passed yet
     * @param task must have not null <code>dateLimits</code>
     * @return <code>true</code> if task date limits is not expire and <code>false</code> otherwise
     */
    public static boolean check(TaskEntity task) {
        Optional<LocalDate> maxLimitDate = task.getDateLimits().getIncludedLimits().stream().map(List::getLast)
                .max(LocalDate::compareTo);
        return
                maxLimitDate.isPresent() &&
                maxLimitDate.get().isAfter(LocalDate.now());
    }
}
