package ru.kusok_piroga.gorzdravbot.recorder.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import ru.kusok_piroga.gorzdravbot.bot.repositories.DialogRepository;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.repositories.CallbackRepository;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.util.Date;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ComponentScan("ru.kusok_piroga.gorzdravbot.recorder.services")
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
class ScheduleServiceTest {

    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private PatientRepository patientRepository;
    @MockBean
    private CallbackRepository callbackRepository;
    @MockBean
    private DialogRepository dialogRepository;

    @MockBean
    private FetchService fetchService;

    @Autowired
    private ScheduleService scheduleService;

    @Test
    void nullNotificationWithEmptyAppointmentListTest() {

        doReturn(emptyList())
                .when(fetchService)
                .getValidAvailableAppointments(any());

        TaskEntity task = new TaskEntity();
        task.setLastNotify(new Date());

        scheduleService.taskProcess(task);

        assertThat(task.getLastNotify())
                .isNull();
        verify(taskRepository, times(1)).save(task);
    }
}