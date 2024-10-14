package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.*;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;
import ru.kusok_piroga.gorzdravbot.bot.services.TaskService;

import static ru.kusok_piroga.gorzdravbot.bot.models.Commands.*;

@TelegramController
@RequiredArgsConstructor
public class MainController {

    private final ApiService api;
    private final LastCommandService lastCommandService;
    private final TaskService taskService;

    @TelegramCommand(COMMAND_START)
    public String onStart(User user) {
        return "Приветствую!\nДля возможности записаться требуется добавить пациента.\nЭто можно сделать командой /add_patient";
    }

    @TelegramCommand(COMMAND_ADD_TASK)
    public TelegramResponse onAddTask(UpdateRequest request) {
        return taskService.execute(request);
    }

    @TelegramRequest(requestType = {RequestType.MESSAGE, RequestType.CALLBACK_QUERY})
    public TelegramResponse onAnyString(UpdateRequest request){
        return lastCommandService.getLastCommandService(request.getChatId())
                .execute(request);
    }
}
