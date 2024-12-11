package ru.kusok_piroga.gorzdravbot.recorder.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.services.RawSendService;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = RecordService.class)
class RecordServiceTest {

    @MockBean
    RawSendService sendService;
    @MockBean
    TaskRepository taskRepository;
    @MockBean
    ApiService api;
    @MockBean
    CallbackEncoder callbackEncoder;

    @Autowired
    RecordService recordService;

    @Test
    void isTimeToRecord_test() {
        TaskEntity task = new TaskEntity();

        assertThat(recordService.isTimeToRecord(task))
                .isFalse();

        task.setLastNotify(LocalDateTime.now());

        assertThat(recordService.isTimeToRecord(task))
                .isFalse();

        LocalDateTime earlierNotifyDate = LocalDateTime.now().minusHours(1);
        task.setLastNotify(earlierNotifyDate);

        assertThat(recordService.isTimeToRecord(task))
                .isTrue();
    }

    @Test
    void makeRecord_test() {
        doReturn(true).when(api).createAppointment(1,"testAppointment","patientId");
        doReturn(false).when(api).createAppointment(1,"wrongTestAppointment","patientId");

        PatientEntity patient = new PatientEntity();
        patient.setPatientId("patientId");

        TaskEntity task = new TaskEntity();
        task.setCompleted(false);
        task.setPolyclinicId(1);
        task.setPatientEntity(patient);

        assertThat(recordService.makeRecord(task, "testAppointment"))
                .isTrue();
        verify(taskRepository, times(1)).save(task);

        task.setCompleted(true);
        assertThat(recordService.makeRecord(task, "testAppointment"))
                .isFalse();
        verify(taskRepository, times(1)).save(task);

        task.setCompleted(false);
        assertThat(recordService.makeRecord(task, "wrongTestAppointment"))
                .isFalse();
        verify(taskRepository, times(1)).save(task);

    }
}