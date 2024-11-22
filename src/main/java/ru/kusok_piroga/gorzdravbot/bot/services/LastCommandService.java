package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.bot.models.DialogEntity;
import ru.kusok_piroga.gorzdravbot.bot.repositories.DialogRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LastCommandService {
    private final TaskCreateCommandService taskCreateCommandService;
    private final PatientCreateCommandService patientCreateCommandService;
    private final StartService startService;
    private final DialogRepository repository;

    public ICommandService getLastCommandService(Long dialogId){
        Optional<DialogEntity> lastCommand = repository.findById(dialogId);
        if (lastCommand.isPresent()){
            log.info("Для чата {} последняя команда - {}", dialogId, lastCommand.get().getLastCommand());
            return switch (lastCommand.get().getLastCommand()){
                case Commands.COMMAND_ADD_TASK -> taskCreateCommandService;
                case Commands.COMMAND_ADD_PATIENT -> patientCreateCommandService;
                default -> startService;
            };
        }
        return startService;
    }

    public void setLastCommand(Long dialogId, String command){
        DialogEntity dialogEntity = new DialogEntity();
        dialogEntity.setId(dialogId);
        dialogEntity.setLastCommand(command);
        repository.save(dialogEntity);

        log.info("Для чата {} обновлена последняя команда - {}", dialogId, command);
    }
}
