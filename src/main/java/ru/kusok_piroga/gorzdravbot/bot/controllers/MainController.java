package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramCommand;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;

@TelegramController
@RequiredArgsConstructor
public class MainController {
    private static final String COMMAND_START = "/start";
    private static final String COMMAND_ADD_PATIENT = "/addpatient";
    private static final String COMMAND_ADD_TASK = "/addtask";

    private final ApiService api;

    @TelegramCommand(COMMAND_START)
    public String onStart(User user) {
        return "Приветствую!\nДля возможности записаться требуется добавить пациента.\nЭто можно сделать командой /add_patient";
    }

    @TelegramRequest(COMMAND_ADD_TASK)
    public TelegramResponse onAddTask(UpdateRequest request) {
        StringBuilder answerText = new StringBuilder();
        answerText.append("Выберите район:\n");
        for (District district : api.getDistricts()){
            answerText.append("%s%n".formatted(district.getName()));
        }
        TelegramResponse response = new GenericTelegramResponse(answerText.toString());

        return response;
    }
}
