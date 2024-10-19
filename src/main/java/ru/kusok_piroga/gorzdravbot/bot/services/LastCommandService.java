package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.bot.models.DialogEntity;
import ru.kusok_piroga.gorzdravbot.bot.repositories.DialogRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LastCommandService {
    private final TaskService taskService;
    private final PatientService patientService;
    private final StartService startService;
    private final DialogRepository repository;

    public ICommandService getLastCommandService(Long dialogId){
        Optional<DialogEntity> lastCommand = repository.findById(dialogId);
        if (lastCommand.isPresent()){
            System.out.println("Для чата %d последняя команда - %s".formatted(dialogId, lastCommand.get().getLastCommand()));
            return switch (lastCommand.get().getLastCommand()){
                case Commands.COMMAND_ADD_TASK -> taskService;
                case Commands.COMMAND_ADD_PATIENT -> patientService;
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

        System.out.println("Для чата %d обновлена последняя команда %s".formatted(dialogId, command));
    }
}
