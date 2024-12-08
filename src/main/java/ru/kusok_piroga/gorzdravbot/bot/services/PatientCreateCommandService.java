package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientState;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientCreateCommandService implements ICommandService {

    private final PatientRepository repository;
    private final PatientService service;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        Long dialogId = request.getChatId();

        service.createPatient(dialogId);

        return new GenericTelegramResponse("Введите фамилию:");
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        Long dialogId = request.getChatId();
        Optional<PatientEntity> patientOpt = repository.findByDialogIdAndStateNot(dialogId, PatientState.COMPLETED);
        if (patientOpt.isEmpty()) {
            return null;
        }

        if (request.getText() == null || request.getText().isEmpty() || request.getText().isBlank()) {
            return new GenericTelegramResponse("Требуется ввести текст (или начать заново с помощью %s)".formatted(Commands.COMMAND_ADD_PATIENT));
        }

        PatientEntity patient = null;
        try {
            patient = service.fillPatientFields(patientOpt.get(), request.getText());
        } catch (DateFormatException e) {
            throw new ru.kusok_piroga.gorzdravbot.bot.exceptions.DateFormatException();
        }

        return switch (patient.getState()) {
            case SET_FIRST_NAME -> new GenericTelegramResponse("Введите имя:");
            case SET_SECOND_NAME -> new GenericTelegramResponse("Введите фамилию:");
            case SET_MIDDLE_NAME -> new GenericTelegramResponse("Введите отчество:");
            case SET_BIRTHDAY -> new GenericTelegramResponse("Введите дату рождения в формате ДД.ММ.ГГГГ:");
            case COMPLETED -> new GenericTelegramResponse("Добавлен - %s %s %s, дата рождения %s".formatted(
                    patient.getSecondName(),
                    patient.getFirstName(),
                    patient.getMiddleName(),
                    patient.getBirthday()
            ));
        };
    }
}
