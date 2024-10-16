package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.TelegramResponse;

public interface ICommandService {
    TelegramResponse execute(UpdateRequest request);
}