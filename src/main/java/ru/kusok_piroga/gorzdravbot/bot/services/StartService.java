package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;

@Service
public class StartService implements ICommandService {

    private static final String MSG_START = """
            Приветствую!
            Для возможности записаться требуется добавить пациента.
            Это можно сделать командой""" + Commands.COMMAND_ADD_PATIENT;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        return new GenericTelegramResponse(MSG_START);
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }
}
