package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.common.InlineButtonTelegramResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskListService implements ICommandService {

    private final TaskRepository repository;

    private static final String MESSAGE_TEXT = """
                                            Задание №%d
                                            ФИО: %s %s %s 
                                            Др: %s
                                            Условия записи: между %s и %s до %s
                                            Поликлиника: %s
                                            Специалист: %s
                                            Врач: %s""";

    @Override
    public TelegramResponse execute(UpdateRequest request) {
        return printTaskList(request.getChatId());
    }

    public List<TaskEntity> getTaskList(long chatId) {
        return repository.findAllByDialogId(chatId);
    }

    public TelegramResponse printTaskList(long chatId) {
        List<TaskEntity> tasks = getTaskList(chatId);

        if (tasks.isEmpty()) {
            return new GenericTelegramResponse("Задачи не найдены. Добавить задачу можно с помощью " + Commands.COMMAND_ADD_TASK);
        }

        DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        return new CompositeTelegramResponse(tasks.stream()
                .map(task -> {
                            Map<String, String> buttons = new HashMap<>();
                            buttons.put("Удалить задачу " + task.getId(), Commands.COMMAND_DELETE_TASK + "/" + task.getId());
                            return new InlineButtonTelegramResponse(
                                    MESSAGE_TEXT.formatted(
                                            task.getId(),
                                            task.getPatientEntity().getSecondName(),
                                            task.getPatientEntity().getFirstName(),
                                            task.getPatientEntity().getMiddleName(),
                                            formater.format(task.getPatientEntity().getBirthday()),
                                            task.getLowTimeLimit(),
                                            task.getHighTimeLimit(),
                                            formater.format(task.getHighDateLimit()),
                                            task.getPolyclinicId(),
                                            task.getSpecialityId(),
                                            task.getDoctorId()
                                    ),
                                    List.of(buttons)
                            );
                        }
                )
                .toList()
        );
    }
}
