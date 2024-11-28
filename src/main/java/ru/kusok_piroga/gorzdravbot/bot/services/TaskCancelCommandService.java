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
public class TaskCancelCommandService implements ICommandService {

    private final TaskService service;

    private static final String MESSAGE_SUCCESS_TEXT = "Номерок '%s' успешно отменён";
    private static final String MESSAGE_FAIL_TEXT = "Номерок не удалось отменить (произошла ошибка или номерок просрочен или уже отменён)";

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return cancelTask(request.getText());
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }

    public TelegramResponse cancelTask(String taskIdStr){
        try {
            Long taskId = Long.parseLong(taskIdStr);
            return cancelTask(taskId);
        } catch (NumberFormatException e){
            log.error("Id parsing error. Str id = '{}'", taskIdStr);
            throw new WrongParamException();
        }
    }
    public TelegramResponse cancelTask(Long taskId){
        try {
            return printSuccessResult(service.cancelAppointmentByTask(taskId));
        } catch (CancelAppointmentException e) {
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
