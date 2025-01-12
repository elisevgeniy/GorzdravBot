package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.TelegramResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.RestartTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskCancelCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskCreateCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskDeleteCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskRestartCommandService;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.SkipAppointmentRepository;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
        TaskCallbackUnit.class,
        TaskRestartCommandService.class,
        TaskService.class
})
class TaskCallbackUnitTest {

    @Autowired
    TaskCallbackUnit taskCallbackUnit;

    @MockitoBean
    TaskDeleteCommandService taskDeleteCommandService;
    @MockitoBean
    TaskCancelCommandService taskCancelCommandService;
    @MockitoBean
    TaskCreateCommandService taskCreateCommandService;

    @Autowired
    TaskRestartCommandService taskRestartCommandService;

    @MockitoBean
    ApiService api;
    @MockitoBean
    TaskRepository repository;
    @MockitoBean
    PatientService patientService;
    @MockitoBean
    SkipAppointmentRepository skipAppointmentRepository;

    @BeforeEach
    void before(){
        TaskEntity task = new TaskEntity();
        task.setDialogId(999L);
        task.setId(1L);
        task.setCompleted(true);
        task.setRecordedAppointmentId("appId");
        task.setLastNotify(LocalDateTime.now());

        PatientEntity patient = new PatientEntity();
        patient.setId(1L);
        task.setPatientEntity(patient);

        doReturn(Optional.of(task)).when(repository).findById(1L);
        doReturn(task).when(repository).save(task);

        doReturn(true).when(api).cancelAppointment(any(),any(),any());
    }

    @Test
    void restart_valid_data() {
        CallbackData data = new CallbackData(
                TaskCallbackUnit.FN_RESTART,
                new RestartTaskDto(1L).toString()
                );

        TelegramResponse response = taskCallbackUnit.execute(999L, data);
        assertThat(response.toString())
                .containsIgnoringCase("успешно перезапущено")
                        .doesNotContainIgnoringCase("Номерок не отменён");

        verify(repository, times(2)).save(any(TaskEntity.class));
    }
}