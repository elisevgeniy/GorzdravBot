package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.domain.repositories.PatientRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientDeleteService implements ICommandService {

    private final PatientRepository repository;
    private final PatientListService patientListService;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {

        if (!request.getMessageTypes().contains(MessageType.COMMAND)
                && request.getText() != null) {
            deletePatient(request.getText());
        }

        return patientListService.printPatientList(request.getChatId());
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
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
}
