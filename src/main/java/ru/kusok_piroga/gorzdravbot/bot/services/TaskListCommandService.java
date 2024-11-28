package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.TaskCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.EmptyTaskListException;
import ru.kusok_piroga.gorzdravbot.bot.responses.InlineButtonTelegramResponse;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskListCommandService implements ICommandService {

    private final TaskService service;
    private final CallbackEncoder callbackEncoder;

    private static final String TASK_DISC = """
            Задание №%d
            ФИО: %s %s %s
            Др: %s
            Условия записи: между %s и %s до %s
            Поликлиника: %s
            Специалист: %s
            Врач: %s
            Номерок: %s""";

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return printTaskList(request.getChatId());
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }

    public TelegramResponse printTaskList(long chatId) {
        List<TaskEntity> completedTasks = service.getCompletedTaskList(chatId);
        List<TaskEntity> uncompletedTask = service.getUncompletedTaskList(chatId);

        if (completedTasks.isEmpty() && uncompletedTask.isEmpty()) {
            throw new EmptyTaskListException();
        }

        List<TelegramResponse> responses = new LinkedList<>();

        if (!completedTasks.isEmpty()) {
            responses.add(new GenericTelegramResponse("Выполненные задачи:"));
            responses.addAll(
                    completedTasks.stream()
                    .map(this::prepareCompletedTaskMessage)
                    .toList());
        }

        if (!uncompletedTask.isEmpty()) {
            responses.add(new GenericTelegramResponse("Не выполненные задачи:"));
            responses.addAll(
                    uncompletedTask.stream()
                    .map(this::prepareUncompletedTaskMessage)
                    .toList()
            );
        }

        return new CompositeTelegramResponse(responses);
    }

    private void addCancelCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Отменить номерок",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_CANCEL,
                        task.getId()
                ));
    }

    private void addDeleteCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Удалить",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_DELETE,
                        task.getId()
                ));
    }

    private TelegramResponse prepareCompletedTaskMessage(TaskEntity task){
        Map<String, String> buttons = new TreeMap<>();
        if (task.getRecordedAppointmentId() != null) {
            addCancelCallbackButton(task, buttons);
        }
        addDeleteCallbackButton(task, buttons);
        return formTaskCallbackButton(task, buttons);
    }

    private TelegramResponse prepareUncompletedTaskMessage(TaskEntity task){
        Map<String, String> buttons = new HashMap<>();
        addDeleteCallbackButton(task, buttons);
        return formTaskCallbackButton(task, buttons);
    }

    @NotNull
    private InlineButtonTelegramResponse formTaskCallbackButton(TaskEntity task, Map<String, String> buttons) {

        DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");

        return new InlineButtonTelegramResponse(
                TASK_DISC.formatted(
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
