package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.PatientCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.EmptyPatientListException;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.bot.responses.InlineButtonTelegramResponse;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientListCommandService implements ICommandService {

    private final CallbackEncoder callbackEncoder;
    private final PatientService service;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return printPatientList(request.getChatId());
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }

    public TelegramResponse printPatientList(long chatId) {
        List<PatientEntity> patients = service.getPatientList(chatId);

        if (patients.isEmpty()) {
            throw new EmptyPatientListException();
        }

        return new CompositeTelegramResponse(List.of(
                new GenericTelegramResponse("Список пациентов:"),
                patients.stream().map(this::printPatientMessage).toList()
        ));
    }

    public TelegramResponse printPatientListForChoose(long chatId) {
        List<PatientEntity> patients = service.getPatientList(chatId);

        if (patients.isEmpty()) {
            throw new EmptyPatientListException();
        }

        List<Map<String, String>> buttons = new ArrayList<>();
        for (PatientEntity patient : patients){
            buttons.add(new HashMap<>());
            buttons.get(buttons.size()-1).put(
                    "%s %s %s".formatted(
                            patient.getSecondName(),
                            patient.getFirstName(),
                            patient.getMiddleName()
                    ),
                    patient.getId().toString()
            );
        }

        return new InlineButtonTelegramResponse("Выберите пациента:", buttons);
    }

    private TelegramResponse printPatientMessage(PatientEntity patient){
        List<Map<String, String>> buttons = new ArrayList<>();
        buttons.add(new TreeMap<>());
        buttons.get(buttons.size()-1).put(
                "Удалить",
                callbackEncoder.encode(
                        PatientCallbackUnit.FN_DELETE,
                        patient.getId()
                )
        );
        return new InlineButtonTelegramResponse(
                "%s %s %s".formatted(
                        patient.getSecondName(),
                        patient.getFirstName(),
                        patient.getMiddleName()
                ),
                buttons
        );
    }
}
