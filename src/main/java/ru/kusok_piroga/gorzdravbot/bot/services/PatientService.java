package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.*;
import ru.kusok_piroga.gorzdravbot.bot.repositories.PatientRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientService implements ICommandService {

    private final ApiService api;
    private final PatientRepository repository;

    @Override
    public TelegramResponse execute(UpdateRequest request) {

        Long dialogId = request.getChatId();

        Optional<PatientEntity> result = repository.findByDialogIdAndStateNot(dialogId, PatientState.COMPLETED);

        if (result.isEmpty()) {
            if (request.getMessage().isCommand()) {
                return patientCreate(dialogId);
            } else {
                return null;
            }
        }

        return patientScenario(result.get(), request.getText());
    }

    public TelegramResponse cleanStart(UpdateRequest request) {
        Long dialogId = request.getChatId();
        repository.deleteByDialogIdAndStateIsNot(dialogId, PatientState.COMPLETED);
        return patientCreate(dialogId);
    }

    private TelegramResponse patientCreate(long dialogId) {
        PatientEntity patient = new PatientEntity();
        patient.setDialogId(dialogId);
        patient.setState(PatientState.SET_SECOND_NAME);
        repository.save(patient);

        return new GenericTelegramResponse("Введите фамилию:");
    }

    private TelegramResponse patientScenario(PatientEntity patient, String message) {
        if (message.isEmpty() || message.isBlank()){
            return new GenericTelegramResponse("Требуется ввести текст (или начать заново с помощью " + Commands.COMMAND_ADD_PATIENT);
        }

        switch (patient.getState()) {
            case INIT -> {
                return null;
            }
            case SET_SECOND_NAME -> {
                patient.setState(PatientState.SET_FIRST_NAME);
                return patientScenarioSetSecondName(patient, message);
            }
            case SET_FIRST_NAME -> {
                patient.setState(PatientState.SET_MIDDLE_NAME);
                return patientScenarioSetFirstName(patient, message);
            }
            case SET_MIDDLE_NAME -> {
                patient.setState(PatientState.SET_BIRTHDAY);
                return patientScenarioSetMiddleName(patient, message);
            }
            case SET_BIRTHDAY -> {
                patient.setState(PatientState.COMPLETED);
                return patientScenarioSetBirthday(patient, message);
            }
            case COMPLETED -> {
                return null;
            }
        }

        return new GenericTelegramResponse("Если что-то пошло не так, вы можете начать создание задачи заново с помощью " + Commands.COMMAND_ADD_PATIENT);
    }

    private TelegramResponse patientScenarioSetSecondName(PatientEntity patient, String message) {
        patient.setSecondName(message);
        repository.save(patient);
        return new GenericTelegramResponse("Введите имя:");
    }

    private TelegramResponse patientScenarioSetFirstName(PatientEntity patient, String message) {
        patient.setFirstName(message);
        repository.save(patient);
        return new GenericTelegramResponse("Введите отчество:");
    }

    private TelegramResponse patientScenarioSetMiddleName(PatientEntity patient, String message) {
        patient.setMiddleName(message);
        repository.save(patient);
        return new GenericTelegramResponse("Введите дату рождения в формате ДД.ММ.ГГГГ:");
    }

    private TelegramResponse patientScenarioSetBirthday(PatientEntity patient, String message) {
        SimpleDateFormat formaterFromMessage = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formaterForBD = new SimpleDateFormat("yyyy-MM-dd");
        formaterForBD.setLenient(false);
        try {
            patient.setBirthday(formaterForBD.format(
                    formaterFromMessage.parse(message)
            ));
            repository.save(patient);

            return new GenericTelegramResponse("Добавлен - %s %s %s, дата рождения %s".formatted(
                    patient.getSecondName(),
                    patient.getFirstName(),
                    patient.getMiddleName(),
                    patient.getBirthday()
            ));

        } catch (ParseException e) {
            return new GenericTelegramResponse("Ошибка формата даты, попробуйте ещё раз");
        }
    }

}
