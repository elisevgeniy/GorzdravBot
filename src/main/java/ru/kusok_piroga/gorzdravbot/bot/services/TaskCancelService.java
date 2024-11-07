package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.callbacks.TaskCallbackChain;
import ru.kusok_piroga.gorzdravbot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.common.responses.InlineButtonTelegramResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCancelService implements ICommandService {

    private final TaskRepository repository;
    private final ApiService api;

    private static final String MESSAGE_SUCCESS_TEXT = "Номерок '%s' успешно отменён";
    private static final String MESSAGE_FAIL_TEXT = "Номерок не удалось отменить (произошла ошибка или номерок просрочен или уже отменён)";

    @Override
    public TelegramResponse execute(UpdateRequest request) {
        return cancelTask(request.getText());
    }

    public TelegramResponse cancelTask(String taskIdStr){
        try {
            Long taskId = Long.parseLong(taskIdStr);
            return cancelTask(taskId);
        } catch (NumberFormatException e){
            log.error("Id parsing error. Str id = '{}'", taskIdStr);
            return printFailResult();
        }
    }
    public TelegramResponse cancelTask(Long taskId){
        Optional<TaskEntity> task = repository.findById(taskId);

        if (task.isEmpty()){
            log.error("Task with id = {} not found", taskId);
            return printFailResult();
        }

        if (api.cancelAppointment(
                task.get().getPolyclinicId(),
                task.get().getRecordedAppointmentId(),
                task.get().getPatientEntity().getPatientId()
        )) {
            task.get().setRecordedAppointmentId(null);
            repository.save(task.get());
            return printSuccessResult(task.get().getRecordedAppointmentId());
        } else  {
            log.warn("Cancel fail. Task id = {}, Appointment id = {}", taskId, task.get().getRecordedAppointmentId());
            return printFailResult();
        }
    }

    private TelegramResponse printSuccessResult(String appointmentId){
        return new GenericTelegramResponse(MESSAGE_SUCCESS_TEXT.formatted(appointmentId));
    }

    private TelegramResponse printFailResult(){
        return new GenericTelegramResponse(MESSAGE_FAIL_TEXT);
    }
}
