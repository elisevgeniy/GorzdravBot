package ru.kusok_piroga.gorzdravbot.recorder.utils;

import org.junit.jupiter.api.Test;
import ru.kusok_piroga.gorzdravbot.SkipAppointmentEntity;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskDateLimits;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskTimeLimits;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintCheckerTest {

    @Test
    void check_valid_time_data() throws TimeLimitParseException, DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("10:00-12:00"));
        task.setDateLimits(new TaskDateLimits("10.10.2024-03.12.2025"));
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
    void check_fail_by_time() throws TimeLimitParseException, DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("20:00-22:00"));
        task.setDateLimits(new TaskDateLimits("10.10.2024-03.12.2025"));
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
    void check_fail_by_date() throws TimeLimitParseException, DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits("10:00-12:00"));
        task.setDateLimits(new TaskDateLimits("10.10.2024-03.12.2025"));
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

    @Test
    void check_valid_skip_data() throws TimeLimitParseException, DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits(""));
        task.setDateLimits(new TaskDateLimits("10.10.2000-03.12.3000"));
        task.setSkippedAppointments(Set.of(
                new SkipAppointmentEntity(
                        1L,
                        task,
                        "1234"
                )
        ));
        AvailableAppointment appointment = new AvailableAppointment(
                "123",
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
    void check_fail_by_skip_data() throws TimeLimitParseException, DateLimitParseException {
        TaskEntity task = new TaskEntity();
        task.setTimeLimits(new TaskTimeLimits(""));
        task.setDateLimits(new TaskDateLimits("10.10.2000-03.12.3000"));
        task.setSkippedAppointments(Set.of(
                new SkipAppointmentEntity(
                        1L,
                        task,
                        "123"
                )
        ));
        AvailableAppointment appointment = new AvailableAppointment(
                "123",
                "2024-11-26T11:35:00",
                "2024-11-26T11:55:00",
                "",
                "",
                ""
        );

        assertThat(ConstraintChecker.check(task, appointment))
                .isFalse();
    }
}