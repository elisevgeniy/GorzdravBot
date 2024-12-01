package ru.kusok_piroga.gorzdravbot.recorder.utils;

import org.junit.jupiter.api.Test;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintCheckerTest {

    @Test
    void check_valid_data() throws TimeLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("10:00-12:00"));
        task.setHighDateLimit(Date.from(Instant
                .parse("2025-12-03T10:15:30.00Z")
                .plus(10, ChronoUnit.DAYS)
        ));
        AvailableAppointment appointment = new AvailableAppointment(
                "",
                "2024-11-26T11:35:00",
                "2024-11-26T11:55:00",
                "",
                "",
                ""
        );

        assertThat(ConstraintChecker.check(task, appointment))
                .isTrue();
    }

    @Test
    void check_fail_by_time() throws TimeLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("20:00-22:00"));
        task.setHighDateLimit(Date.from(Instant
                .parse("2025-12-03T10:15:30.00Z")
                .plus(10, ChronoUnit.DAYS)
        ));
        AvailableAppointment appointment = new AvailableAppointment(
                "",
                "2024-11-26T11:35:00",
                "2024-11-26T11:55:00",
                "",
                "",
                ""
        );

        assertThat(ConstraintChecker.check(task, appointment))
                .isFalse();
    }

    @Test
    void check_fail_by_date() throws TimeLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("10:00-12:00"));
        task.setHighDateLimit(Date.from(Instant
                .parse("2025-12-03T10:15:30.00Z")
                .plus(10, ChronoUnit.DAYS)
        ));
        AvailableAppointment appointment = new AvailableAppointment(
                "",
                "2026-11-26T11:35:00",
                "2026-11-26T11:55:00",
                "",
                "",
                ""
        );

        assertThat(ConstraintChecker.check(task, appointment))
                .isFalse();
    }
}