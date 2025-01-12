package ru.kusok_piroga.gorzdravbot.recorder.utils;

import org.junit.jupiter.api.Test;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PassTimeCheckerTest {

    @Test
    void check_valid_date() throws DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setDateLimits(new TaskDateLimits("01.01.2000 - 01.01.2000, 01.01.2100 - 01.01.2100"));

        assertThat(PassTimeChecker.check(task))
                .isTrue();
    }

    @Test
    void check_fail_by_passed_date() throws DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setDateLimits(new TaskDateLimits("01.01.2000"));

        assertThat(PassTimeChecker.check(task))
                .isFalse();
    }
}