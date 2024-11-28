package ru.kusok_piroga.gorzdravbot.producer.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public PatientEntity fillPatientFields(PatientEntity patient, String value) throws DateFormatException {
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

    private void setBirthday(PatientEntity patient, String message) throws DateFormatException {
        SimpleDateFormat formaterFromMessage = new SimpleDateFormat("dd.MM.yyyy");
        formaterFromMessage.setLenient(false);
        try {
            patient.setBirthday(
                    formaterFromMessage.parse(message)
            );
        } catch (ParseException e) {
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
