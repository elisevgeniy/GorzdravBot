package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.*;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;

@TelegramController
@RequiredArgsConstructor
public class MassageController {

    private final LastCommandService lastCommandService;

    @TelegramRequest(requestType = {RequestType.MESSAGE})
    public TelegramResponse onAnyMessage(UpdateRequest request){
        return lastCommandService.getLastCommandService(request.getChatId())
                .processMessage(request);
    }
}
