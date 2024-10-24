package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.common.repositories.PatientRepository;

@Service
@RequiredArgsConstructor
public class PatientDeleteService implements ICommandService {

    private final PatientRepository repository;
    private final PatientListService patientListService;

    @Override
    public TelegramResponse execute(UpdateRequest request) {

        if (!request.getMessageTypes().contains(MessageType.COMMAND)
                && request.getText() != null && !request.getText().isBlank() && !request.getText().isEmpty()) {
            repository.deleteById(Long.parseLong(request.getText()));
        }

        return patientListService.printPatientList(request.getChatId());
    }
}
