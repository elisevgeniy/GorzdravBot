package ru.kusok_piroga.gorzdravbot.bot.controllers;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;

@Component
@TelegramScope
@RequiredArgsConstructor
public class MainFilter implements PriorityPreUpdateFilter, PostUpdateFilter {
    private final LastCommandService lastCommandService;

    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        System.out.printf("В чат %d пришло сообщение \"%s\" типа %s. Тип запроса - %s %n", request.getChatId(), request.getText(), request.getMessageTypes(), request.getRequestType());
    }

    @Override
    public void postFilter(@NonNull UpdateRequest request) {
        if (request.getMessage() != null && request.getMessage().isCommand()){
            lastCommandService.setLastCommand(request.getChatId(), request.getText());
        }
    }

    @Override
    public int getPreOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}