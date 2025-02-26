package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.ChangeTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.CopyTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.FastRecordTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.RestartTaskDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.TaskCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.EmptyTaskListException;
import ru.kusok_piroga.gorzdravbot.bot.responses.InlineButtonTelegramResponse;
import ru.kusok_piroga.gorzdravbot.domain.models.SkipAppointmentEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.time.format.DateTimeFormatter;
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
            Условия записи:
            - по времени: %s
            - по датам: %s
            Поликлиника: %s
            Специалист: %s
            Врач: %s
            Номерок: %s
            Пропускаются номерки:
            %s""";

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

    private void addRestartCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Перезапустить",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_RESTART,
                        new RestartTaskDto(task.getId())
                ));
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

    private void addChangeCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Изменить",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_CHANGE,
                        new ChangeTaskDto(task.getId())
                ));
    }

    private void addCopyCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put("Копировать",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_COPY,
                        new CopyTaskDto(task.getId())
                ));
    }

    private void addFastRecordCallbackButton(TaskEntity task, Map<String, String> buttons) {
        buttons.put(
                (task.getLastNotify() == null ? "Вкл" : "Выкл") + " мгновенную запись",
                callbackEncoder.encode(
                        TaskCallbackUnit.FN_FAST_RECORD,
                        new FastRecordTaskDto(task.getId())
                ));
    }

    private TelegramResponse prepareCompletedTaskMessage(TaskEntity task){
        List<Map<String, String>> buttons = new ArrayList<>();
        buttons.add(new LinkedHashMap<>());
        buttons.add(new LinkedHashMap<>());
        buttons.add(new LinkedHashMap<>());

        addChangeCallbackButton(task, buttons.get(0));
        addCopyCallbackButton(task, buttons.get(0));
        addRestartCallbackButton(task, buttons.get(1));
        addDeleteCallbackButton(task, buttons.get(1));
        if (task.getRecordedAppointmentId() != null) {
            addCancelCallbackButton(task, buttons.get(2));
        }

        return formTaskCallbackButton(task, buttons);
    }

    private TelegramResponse prepareUncompletedTaskMessage(TaskEntity task){
        List<Map<String, String>> buttons = new ArrayList<>();
        buttons.add(new LinkedHashMap<>());
        buttons.add(new LinkedHashMap<>());
        buttons.add(new LinkedHashMap<>());
        addChangeCallbackButton(task, buttons.get(0));
        addCopyCallbackButton(task, buttons.get(0));
        addDeleteCallbackButton(task, buttons.get(1));
        addFastRecordCallbackButton(task, buttons.get(2));
        return formTaskCallbackButton(task, buttons);
    }

    @NotNull
    private InlineButtonTelegramResponse formTaskCallbackButton(TaskEntity task, List<Map<String, String>> buttons) {

        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return new InlineButtonTelegramResponse(
                TASK_DISC.formatted(
                        task.getId(),
                        task.getPatientEntity().getSecondName(),
                        task.getPatientEntity().getFirstName(),
                        task.getPatientEntity().getMiddleName(),
                        task.getPatientEntity().getBirthday().format(formater),
                        (task.getTimeLimits().toString().isEmpty()) ? "нет" : task.getTimeLimits(),
                        task.getDateLimits(),
                        task.getPolyclinicId(),
                        task.getSpecialityId(),
                        task.getDoctorId(),
                        (task.getRecordedAppointmentId() == null) ?
                                "не взят"
                                :
                                task.getRecordedAppointmentId(),
                        (task.getSkippedAppointments().isEmpty()) ?
                                "нет"
                                :
                                task.getSkippedAppointments().stream()
                                        .map(SkipAppointmentEntity::getAppointmentId)
                                        .toList().toString()
                ),
                buttons
        );
    }
}
