package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;

import java.util.ArrayList;
import java.util.List;

@Component
@TelegramScope
@RequiredArgsConstructor
public class DeleteChatFilter implements PriorityPreUpdateFilter {
    private final LastCommandService lastCommandService;

    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        if (request.getMessage() != null) {
            deleteChat(request.getAbsSender(), request.getChatId(), request.getMessage().getMessageId());
        }
    }

    private void deleteChat(TelegramClient client, long chatId, int lastMessageId) {
        List<Integer> ids = new ArrayList<>(100);
        ids.add(lastMessageId);
        for (int i = 1; i < 100; i++) {
            ids.add(lastMessageId - i);
        }
        try {
            client.execute(DeleteMessages.builder()
                    .chatId(chatId)
                    .messageIds(ids)
                    .build()
            );
        } catch (TelegramApiException ignored) {
        }
    }
}