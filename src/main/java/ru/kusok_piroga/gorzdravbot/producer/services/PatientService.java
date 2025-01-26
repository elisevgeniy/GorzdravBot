package ru.kusok_piroga.gorzdravbot.producer.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;

    public void createPatient(long dialogId) {
        clearUncompletedPatient(dialogId);

        PatientEntity patient = new PatientEntity();

        patient.setDialogId(dialogId);
        patient.setState(PatientState.SET_SECOND_NAME);

        repository.save(patient);
    }

    public void clearUncompletedPatient(Long dialogId) {
        repository.deleteByDialogIdAndStateIsNot(dialogId, PatientState.COMPLETED);
    }

    @Transactional(rollbackOn = Exception.class)
    public PatientEntity fillPatientFields(long patientId, String value) throws DateFormatException {
        PatientEntity patient = getPatient(patientId);

        patient = switch (patient.getState()) {
            case COMPLETED -> patient;
            case SET_SECOND_NAME -> {
                patient.setState(PatientState.SET_FIRST_NAME);
                patient.setSecondName(value);
                yield repository.save(patient);
            }
            case SET_FIRST_NAME -> {
                patient.setState(PatientState.SET_MIDDLE_NAME);
                patient.setFirstName(value);
                yield repository.save(patient);
            }
            case SET_MIDDLE_NAME -> {
                patient.setState(PatientState.SET_BIRTHDAY);
                patient.setMiddleName(value);
                yield repository.save(patient);
            }
            case SET_BIRTHDAY -> {
                patient.setState(PatientState.COMPLETED);
                setBirthday(patient, value);
                yield repository.save(patient);
            }
        };
        return patient;
    }

    private PatientEntity getPatient(long id){
        try {
            return repository.findByIdWithLock(id).orElseThrow();
        } catch (PessimisticLockingFailureException e){
            log.error("Task, id={}, process error, db row is locked", id, e);
            return null;
        }
    }

    private void setBirthday(PatientEntity patient, String message) throws DateFormatException {
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            patient.setBirthday(
                    LocalDate.parse(message, formater)
            );
        } catch (DateTimeParseException e) {
            throw new DateFormatException();
        }
    }

    public Optional<PatientEntity>  getPatientById(long patientId) {
        return repository.findById(patientId);
    }

    public void savePatient(PatientEntity patient) {
        repository.save(patient);
    }

    public void deletePatient(String idStr){
        try {
            deletePatient(Long.parseLong(idStr));
        } catch (NumberFormatException e){
            log.error("Wrong patient id: {}", idStr);
        }
    }

    public void deletePatient(@NonNull Long id){
        log.info("Delete patient id: {}", id);
        repository.deleteById(id);
    }

    public List<PatientEntity> getPatientList(long chatId) {
        return repository.findCompletedByDialogId(chatId);
    }
}
