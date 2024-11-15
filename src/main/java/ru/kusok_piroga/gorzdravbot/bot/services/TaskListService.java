package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.callbacks.TaskCallbackChain;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.common.responses.InlineButtonTelegramResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskListService implements ICommandService {

    private final TaskRepository repository;
    private final CallbackEncoder callbackEncoder;

    private static final String MESSAGE_TEXT = """
            Задание №%d
            ФИО: %s %s %s
            Др: %s
            Условия записи: между %s и %s до %s
            Поликлиника: %s
            Специалист: %s
            Врач: %s
            Номерок: %s""";

    @Override
    public TelegramResponse execute(UpdateRequest request) {
        return printTaskList(request.getChatId());
    }

    public List<TaskEntity> getCompletedTaskList(long chatId) {
        return repository.findAllCompletedTasksByDialogId(chatId);
    }

    public List<TaskEntity> getUncompletedTaskList(long chatId) {
        return repository.findAllUncompletedTasksByDialogId(chatId);
    }

    public TelegramResponse printTaskList(long chatId) {
        List<TaskEntity> completedTasks = getCompletedTaskList(chatId);
        List<TaskEntity> uncompletedTask = getUncompletedTaskList(chatId);

        if (completedTasks.isEmpty() && uncompletedTask.isEmpty()) {
            return new GenericTelegramResponse("Задачи не найдены. Добавить задачу можно с помощью " + Commands.COMMAND_ADD_TASK);
        }

        List<TelegramResponse> responses = new LinkedList<>();

        responses.add(new GenericTelegramResponse("Выполненные задачи:"));
        responses.addAll(completedTasks.stream()
                .map(task -> {
                            Map<String, String> buttons = new TreeMap<>();
                            if (task.getRecordedAppointmentId() != null){
                                addCancelCallbackButton(task, buttons);
                            }
                            addDeleteCallbackButton(task, buttons);
                            return formTaskCallbackButton(task, buttons);

                        }
                )
                .toList());

        responses.add(new GenericTelegramResponse("Не выполненные задачи:"));
        responses.addAll(uncompletedTask.stream()
                .map(task -> {
                            Map<String, String> buttons = new HashMap<>();
                            addDeleteCallbackButton(task, buttons);
                            return formTaskCallbackButton(task, buttons);
                        }
                )
                .toList()
        );

        return new CompositeTelegramResponse(responses);
    }

    private void addCancelCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Отменить номерок",
                callbackEncoder.encode(
                        TaskCallbackChain.FN_CANCEL,
                        task.getId()
                ));
    }

    private void addDeleteCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Удалить",
                callbackEncoder.encode(
                        TaskCallbackChain.FN_DELETE,
                        task.getId()
                ));
    }

    @NotNull
    private InlineButtonTelegramResponse formTaskCallbackButton(TaskEntity task, Map<String, String> buttons) {

        DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");

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
                        task.getDoctorId(),
                        (task.getRecordedAppointmentId() == null) ?
                                "не взят"
                                :
                                task.getRecordedAppointmentId()
                ),
                List.of(buttons)
        );
    }
}
