package ru.kusok_piroga.gorzdravbot.recorder.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.bot.services.RawSendService;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ScheduleTaskService.class)
class ScheduleTaskServiceTest {
    @MockitoBean
    TaskRepository taskRepository;
    @MockitoBean
    FetchService fetchService;
    @MockitoBean
    NotifyService notifyService;
    @MockitoBean
    RecordService recordService;
    @MockitoBean
    RawSendService sendService;

    @Autowired
    private ScheduleTaskService scheduleTaskService;


    @BeforeEach
    void setUp() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setDialogId(10L);
        task.setRecordedAppointmentId("testAppId");

        doReturn(Optional.of(task)).when(taskRepository).findById(1L);
        doReturn(Optional.of(task)).when(taskRepository).findTaskByIdWithLock(1L);
        doReturn(task).when(taskRepository).save(task);
    }

    @Test
    void success_notification(){
        TaskEntity task = taskRepository.findById(1L).orElseThrow();

        doReturn(true).when(notifyService).needNotify(task);
        doReturn(true).when(notifyService).notifyToChat(eq(task), any());
        doReturn(List.of(new AvailableAppointment("","","","","","")))
                .when(fetchService).getValidAvailableAppointments(task);

        scheduleTaskService.taskProcess(task.getId());

        verify(taskRepository).save(task);
        assertThat(task.getLastNotify())
                .isNotNull();
    }

    @Test
    void success_record(){
        TaskEntity task = taskRepository.findById(1L).orElseThrow();

        doReturn(false).when(notifyService).needNotify(task);
        doReturn(true).when(recordService).isTimeToRecord(task);
        doReturn(true).when(recordService).makeRecord(eq(task), anyList());
        doReturn(List.of(new AvailableAppointment("123","","","","","")))
                .when(fetchService).getValidAvailableAppointments(task);

        scheduleTaskService.taskProcess(task.getId());

        verify(recordService).makeRecord(eq(task), anyList());
    }

    @Test
    void nullification_notification_if_empty_appointment_list() {

        doReturn(emptyList())
                .when(fetchService)
                .getValidAvailableAppointments(any());

        TaskEntity task = taskRepository.findById(1L).orElseThrow();
        task.setLastNotify(LocalDateTime.now());

        scheduleTaskService.taskProcess(task.getId());

        assertThat(task.getLastNotify())
                .isNull();
        verify(taskRepository, times(1)).save(task);
    }
}