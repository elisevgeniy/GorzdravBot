package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
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
public class MainFilter implements PriorityPreUpdateFilter, PostUpdateFilter {
    private final LastCommandService lastCommandService;

    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        System.out.printf("В чат %d пришло сообщение \"%s\" типа %s. Тип запроса - %s %n", request.getChatId(), request.getText(), request.getMessageTypes(), request.getRequestType());

        if (request.getMessage() != null) {
            deleteChat(request.getAbsSender(), request.getChatId(), request.getMessage().getMessageId());
        }
    }

    @Override
    public void postFilter(@NonNull UpdateRequest request) {
        if (request.getMessage() != null && request.getMessage().isCommand()) {
            lastCommandService.setLastCommand(request.getChatId(), request.getText());
        }
    }

    @Override
    public int getPreOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
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
            throw new RuntimeException(ignored);
        }
    }
}