package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.TelegramAdvice;
import io.github.drednote.telegram.core.annotation.TelegramExceptionHandler;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.extern.slf4j.Slf4j;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.CreatePatientException;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateFormatException;

@Slf4j
@TelegramAdvice
public class ExceptionAdviceController {

    @TelegramExceptionHandler(DateFormatException.class)
    private TelegramResponse onDateFormatException(UpdateRequest request) {
        if (request.getError() == null) {
            return null;
        }

        log.error("DateFormatException. Dialog id = '{}', message text = '{}'", request.getChatId(), request.getText());
        return new GenericTelegramResponse("Ошибка формата даты. Попробуйте ещё");
    }

    @TelegramExceptionHandler(CreatePatientException.class)
    private TelegramResponse onCreatePatientException(UpdateRequest request) {
        if (request.getError() == null) {
            return null;
        }

        log.error("CreatePatientException. Dialog id = '{}', message text = '{}'", request.getChatId(), request.getText());
        return new GenericTelegramResponse("Ошибка при создании пациента");

    }

    private void sendMessage(UpdateRequest request, String message) {
        ResponseSetter.setResponse(request, new GenericTelegramResponse(message));
    }
}
