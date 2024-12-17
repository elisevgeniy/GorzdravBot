package ru.kusok_piroga.gorzdravbot.recorder.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ScheduleService.class)
class ScheduleServiceTest {

    @MockitoBean
    TaskRepository taskRepository;
    @MockitoBean
    FetchService fetchService;
    @MockitoBean
    NotifyService notifyService;
    @MockitoBean
    RecordService recordService;

    @Autowired
    private ScheduleService scheduleService;

    @Test
    void success_notification(){
        TaskEntity task = new TaskEntity();

        doReturn(task).when(taskRepository).save(task);
        doReturn(true).when(notifyService).needNotify(task);
        doReturn(true).when(notifyService).notifyToChat(eq(task), any());
        doReturn(List.of(new AvailableAppointment("","","","","","")))
                .when(fetchService).getValidAvailableAppointments(task);

        scheduleService.taskProcess(task);

        verify(taskRepository).save(task);
        assertThat(task.getLastNotify())
                .isNotNull();
    }

    @Test
    void success_record(){
        TaskEntity task = new TaskEntity();

        doReturn(task).when(taskRepository).save(task);
        doReturn(false).when(notifyService).needNotify(task);
        doReturn(true).when(recordService).isTimeToRecord(task);
        doReturn(true).when(recordService).makeRecord(eq(task), anyList());
        doReturn(List.of(new AvailableAppointment("123","","","","","")))
                .when(fetchService).getValidAvailableAppointments(task);

        scheduleService.taskProcess(task);

        verify(recordService).makeRecord(eq(task), anyList());
    }

    @Test
    void nullification_notification_if_empty_appointment_list() {

        doReturn(emptyList())
                .when(fetchService)
                .getValidAvailableAppointments(any());

        TaskEntity task = new TaskEntity();
        task.setLastNotify(LocalDateTime.now());

        scheduleService.taskProcess(task);

        assertThat(task.getLastNotify())
                .isNull();
        verify(taskRepository, times(1)).save(task);
    }
}