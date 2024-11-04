package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.callbacks.PatientCallbackChain;
import ru.kusok_piroga.gorzdravbot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.common.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.common.repositories.PatientRepository;
import ru.kusok_piroga.gorzdravbot.common.responses.InlineButtonTelegramResponse;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientListService implements ICommandService {

    private final PatientRepository repository;
    private final CallbackEncoder callbackEncoder;

    @Override
    public TelegramResponse execute(UpdateRequest request) {
        return printPatientList(request.getChatId());
    }

    public Optional<PatientEntity>  getPatientById(long patientId) {
        return repository.findById(patientId);
    }

    public void savePatient(PatientEntity patient) {
        repository.save(patient);
    }

    public List<PatientEntity>  getPatientList(long chatId) {
        return repository.findCompletedByDialogId(chatId);
    }

    public TelegramResponse printPatientList(long chatId) {
        List<PatientEntity> patients = getPatientList(chatId);

        if (patients.isEmpty()) {
            return new GenericTelegramResponse("Пациенты не найдены. Добавить пациента можно с помощью " + Commands.COMMAND_ADD_PATIENT);
        }

        return new CompositeTelegramResponse(List.of(
                new GenericTelegramResponse("Список пациентов:"),
                patients.stream().map(this::getPatientMessage).toList()
        ));
    }
    public TelegramResponse printPatientListForChoose(long chatId) {
        List<PatientEntity> patients = getPatientList(chatId);

        if (patients.isEmpty()) {
            return new GenericTelegramResponse("Пациенты не найдены. Добавить пациента можно с помощью " + Commands.COMMAND_ADD_PATIENT);
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

    private TelegramResponse getPatientMessage(PatientEntity patient){
        List<Map<String, String>> buttons = new ArrayList<>();
        buttons.add(new TreeMap<>());
        buttons.get(buttons.size()-1).put(
                "Удалить",
                callbackEncoder.encode(
                        PatientCallbackChain.FN_DELETE,
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
