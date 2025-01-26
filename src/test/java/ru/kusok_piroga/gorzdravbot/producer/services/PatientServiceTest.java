package ru.kusok_piroga.gorzdravbot.producer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PatientService.class)
class PatientServiceTest {

    @MockitoBean
    PatientRepository patientRepository;

    @Autowired
    PatientService patientService;

    @BeforeEach
    void setUp() {
        PatientEntity patient = new PatientEntity();
        patient.setId(1L);
        patient.setFirstName("fn");
        patient.setSecondName("sn");
        patient.setMiddleName("mn");
        patient.setBirthday(LocalDate.now());

        doReturn(Optional.of(patient)).when(patientRepository).findById(1L);
        doReturn(Optional.of(patient)).when(patientRepository).findByIdWithLock(1L);
    }

    @Test
    void createPatient() {
        patientService.createPatient(1L);

        ArgumentCaptor<PatientEntity> capturePatient = ArgumentCaptor.forClass(PatientEntity.class);
        verify(patientRepository).save(capturePatient.capture());
        PatientEntity savedPatient = capturePatient.getValue();

        assertThat(savedPatient.getState()).isSameAs(PatientState.SET_SECOND_NAME);
    }

    static Stream<Arguments> getPatientStates(){
        return Stream.of(
                Arguments.of(PatientState.SET_SECOND_NAME, PatientState.SET_FIRST_NAME, "sn"),
                Arguments.of(PatientState.SET_FIRST_NAME, PatientState.SET_MIDDLE_NAME, "fn"),
                Arguments.of(PatientState.SET_MIDDLE_NAME, PatientState.SET_BIRTHDAY, "md"),
                Arguments.of(PatientState.SET_BIRTHDAY, PatientState.COMPLETED, "01.01.1990")
        );
    }

    @ParameterizedTest
    @MethodSource("getPatientStates")
    void fillPatientFields_change_state(PatientState init, PatientState target, String value) throws DateFormatException {
        PatientEntity patient = patientRepository.findById(1L).orElseThrow();
        patient.setState(init);

        patientService.fillPatientFields(patient.getId(), value);

        ArgumentCaptor<PatientEntity> patientCapture = ArgumentCaptor.forClass(PatientEntity.class);
        verify(patientRepository).save(patientCapture.capture());
        PatientEntity savedPatient = patientCapture.getValue();

        assertThat(savedPatient.getState()).isSameAs(target);
    }

    @Test
    void fillPatientFields_fail_by_date() {
        PatientEntity patient = patientRepository.findById(1L).orElseThrow();
        patient.setState(PatientState.SET_BIRTHDAY);

        assertThatThrownBy(()->patientService.fillPatientFields(patient.getId(), "122345"))
                .isInstanceOf(DateFormatException.class);

        verify(patientRepository, times(0)).save(any());
    }
}