package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.WrongParamException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.CancelAppointmentException;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskRestartCommandService implements ICommandService {

    private final TaskService service;

    private static final String MESSAGE_RESTART_FULL_SUCCESS = """
            Задание №%d успешно перезапущено.
            Номерок %s отменён""";
    private static final String MESSAGE_RESTART_HALF_SUCCESS = """
            Задание №%d успешно перезапущено.
            Номерок не отменён""";
    private static final String MESSAGE_RESTART_FULL_FAILED = """
            Задание №%d не перезапущено.
            Номерок не отменён""";
    private static final String MESSAGE_RESTART_HALF_FAILED = """
            Задание №%d не перезапущено.
            Номерок %s отменён""";

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return null;
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }


    public TelegramResponse restartTask(String taskIdStr){
        try {
            Long taskId = Long.parseLong(taskIdStr);
            return restartTask(taskId);
        } catch (NumberFormatException e){
            log.error("Id parsing error. Str id = '{}'", taskIdStr);
            throw new WrongParamException();
        }
    }
    public TelegramResponse restartTask(Long taskId){
        boolean appoinmentCalceled = false;
        String appoinmentId = null;
        try {
            appoinmentId = service.cancelAppointmentByTask(taskId);
            appoinmentCalceled = true;
        } catch (CancelAppointmentException ignored) {}
        boolean taskRestarted = service.restartTask(taskId);

        if (taskRestarted){
            if (appoinmentCalceled) {
                return new GenericTelegramResponse(MESSAGE_RESTART_FULL_SUCCESS.formatted(
                        taskId,
                        appoinmentId
                ));
            } else {
                return new GenericTelegramResponse(MESSAGE_RESTART_HALF_SUCCESS.formatted(
                        taskId
                ));
            }
        } else {
            if (appoinmentCalceled) {
                return new GenericTelegramResponse(MESSAGE_RESTART_HALF_FAILED.formatted(
                        taskId,
                        appoinmentId
                ));
            } else {
                return new GenericTelegramResponse(MESSAGE_RESTART_FULL_FAILED.formatted(
                        taskId
                ));
            }
        }
    }
}
