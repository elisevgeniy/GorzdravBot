package ru.kusok_piroga.gorzdravbot.producer.services;

import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.CancelAppointmentException;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TaskService.class)
class TaskServiceTest {

    @MockBean
    ApiService apiService;
    @MockBean
    TaskRepository taskRepository;
    @MockBean
    PatientService patientService;
    @MockBean
    TaskValidator validator;

    @Autowired
    TaskService taskService;

    @BeforeEach
    void setUp() {
        PatientEntity patient = new PatientEntity();
        patient.setId(1L);
        patient.setFirstName("fn");
        patient.setSecondName("sn");
        patient.setMiddleName("mn");
        patient.setBirthday(new Date());

        TaskEntity task = new TaskEntity();
        task.setRecordedAppointmentId("testAppId");
        task.setPatientEntity(patient);

        doReturn(Optional.of(task)).when(taskRepository).findById(1L);
        doReturn(Optional.of(patient)).when(patientService).getPatientById(1L);
        doReturn(task).when(taskRepository).save(task);
    }

    @Test
    void createTask()  {
        taskService.createTask(1L);

        verify(taskRepository).save(any(TaskEntity.class));
        verify(taskRepository).deleteByDialogIdAndStateIsNot(1L, TaskState.SETUPED);

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(TaskState.SET_DISTRICT);
    }

    @Test
    void cancelAppointment_success_valid_data() {
        doReturn(true).when(apiService).cancelAppointment(any(),any(),any());

        try {
            assertThat(taskService.cancelAppointmentByTask(1L)).isEqualTo("testAppId");
            verify(apiService).cancelAppointment(any(),any(),any());
            verify(taskRepository).save(any(TaskEntity.class));
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        reset(apiService);
    }

    @Test
    void cancelAppointment_fail_valid_data() {
        doReturn(false).when(apiService).cancelAppointment(any(),any(),any());

        assertThatThrownBy(()-> taskService.cancelAppointmentByTask(1L))
                .isInstanceOf(CancelAppointmentException.class);
        verify(apiService).cancelAppointment(any(),any(),any());
        verify(taskRepository, times(0)).save(any(TaskEntity.class));
    }

    @Test
    void cancelAppointment_fail_by_invalid_data() {
        doReturn(true).when(apiService).cancelAppointment(any(),any(),any());

        assertThatThrownBy(()-> taskService.cancelAppointmentByTask(2L))
                .isInstanceOf(NoSuchElementException.class);
        verify(apiService, times(0)).cancelAppointment(any(),any(),any());
        verify(taskRepository, times(0)).save(any(TaskEntity.class));
    }

    @ParameterizedTest
    @MethodSource("taskStateParamsWithoutTimeDatePatient")
    void fillTaskFields_state_changes_without_time_date_patient(TaskState initState, TaskState targetState, String data) {
        TaskEntity task = new TaskEntity();
        task.setState(initState);

        try {
            taskService.fillTaskFields(task, data);
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(targetState);
    }

    static Stream<Arguments> taskStateParamsWithoutTimeDatePatient(){
        return Stream.of(
                Arguments.of(TaskState.SET_DISTRICT, TaskState.SET_POLYCLINIC, "1"),
                Arguments.of(TaskState.SET_POLYCLINIC, TaskState.SET_SPECIALITY, "1"),
                Arguments.of(TaskState.SET_SPECIALITY, TaskState.SET_DOCTOR, "1"),
                Arguments.of(TaskState.SET_DOCTOR, TaskState.SET_PATIENT, "1")
        );
    }


    @Test
    void fillTaskFields_state_changes_for_time_limits() {
        TaskEntity task = new TaskEntity();
        task.setState(TaskState.SET_TIME_LOW_LIMITS);

        try {
            taskService.fillTaskFields(task, "10:00");
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(TaskState.SET_TIME_HIGH_LIMITS);

        try {
            taskService.fillTaskFields(task, "12:00");
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        verify(taskRepository, times(2)).save(taskCaptor.capture());
        savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(TaskState.SET_DATE_LIMITS);
    }

    @Test
    void fillTaskFields_state_changes_for_date_limits() {
        TaskEntity task = new TaskEntity();
        task.setState(TaskState.SET_DATE_LIMITS);

        try {
            taskService.fillTaskFields(task, "10.10.2020");
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(TaskState.SETUPED);
    }

    @Test
    void fillTaskFields_state_changes_for_patient() {
        doReturn("patientId").when(apiService).getPatientId(any(),any(),any(),any(),any());

        TaskEntity task = taskRepository.findById(1L).orElseThrow();
        task.setState(TaskState.SET_PATIENT);

        try {
            taskService.fillTaskFields(task, "1");
        } catch (Exception e) {
            throw new AssertionFailure(e.getMessage());
        }

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertThat(savedTask.getState()).isSameAs(TaskState.SET_TIME_LOW_LIMITS);
    }

}