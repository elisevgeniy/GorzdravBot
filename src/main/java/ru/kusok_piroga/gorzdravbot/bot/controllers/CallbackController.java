package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.CallbackChainController;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;

import java.util.Optional;

@TelegramController
@RequiredArgsConstructor
public class CallbackController {

    private final MassageController massageController;

    private final CallbackChainController callbackChainController;
    private final CallbackEncoder callbackEncoder;

    @TelegramRequest(requestType = {RequestType.CALLBACK_QUERY})
    public TelegramResponse onAnyCallback(UpdateRequest request){
        Optional<CallbackData> callbackData = callbackEncoder.decode(request.getText());

        if (callbackData.isEmpty()){
            return massageController.onAnyMessage(request);
        }

        return callbackChainController.run(callbackData.get());

    }
}
