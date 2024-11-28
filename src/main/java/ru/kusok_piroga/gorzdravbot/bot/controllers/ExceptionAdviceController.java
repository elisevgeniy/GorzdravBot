package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramAdvice;
import io.github.drednote.telegram.core.annotation.TelegramExceptionHandler;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.extern.slf4j.Slf4j;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.AbstractBotException;

@Slf4j
@TelegramAdvice
public class ExceptionAdviceController {

    @TelegramExceptionHandler(AbstractBotException.class)
    private TelegramResponse onBotException(UpdateRequest request) {
        if (request.getError() == null) {
            return null;
        }
        log.error("Dialog id = '{}', message text = '{}', exception: {}", request.getChatId(), request.getText(), request.getError().getClass());
        return new GenericTelegramResponse(request.getError().getMessage());
    }
}
