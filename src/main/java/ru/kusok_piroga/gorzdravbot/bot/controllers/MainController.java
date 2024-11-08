package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.*;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.callbacks.CallbackChain;
import ru.kusok_piroga.gorzdravbot.bot.services.*;
import ru.kusok_piroga.gorzdravbot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.callbacks.models.CallbackData;

import java.util.Optional;

@TelegramController
@RequiredArgsConstructor
public class MainController {

    private final LastCommandService lastCommandService;
    private final CallbackChain callbackChain;
    private final CallbackEncoder callbackEncoder;

    @TelegramRequest(requestType = {RequestType.CALLBACK_QUERY})
    public TelegramResponse onAnyCallback(UpdateRequest request){
        Optional<CallbackData> callbackData = callbackEncoder.decode(request.getText());

        if (callbackData.isEmpty()){
            return onAnyMessage(request);
        }

        return callbackChain.run(callbackData.get());

    }
    @TelegramRequest(requestType = {RequestType.MESSAGE})
    public TelegramResponse onAnyMessage(UpdateRequest request){
        return lastCommandService.getLastCommandService(request.getChatId())
                .execute(request);
    }
}
