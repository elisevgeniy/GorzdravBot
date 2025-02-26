package ru.kusok_piroga.gorzdravbot.bot.filters;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.services.LastCommandService;

/**
 * Filter works as proxy for request. It updates last user command for chat.
 */
@Slf4j
@Component
@TelegramScope
@RequiredArgsConstructor
public class SetLastCommandFilter implements PriorityPreUpdateFilter, PostUpdateFilter {
    private final LastCommandService lastCommandService;

    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        log.info("Dialog id = '{}', message text=\"{}\", message type = '{}', request type = '{}'", request.getChatId(), request.getText(), request.getMessageTypes(), request.getRequestType());
    }

    @Override
    public void postFilter(@NonNull UpdateRequest request) {
        if (request.getMessage() != null && request.getMessage().isCommand()) {
            lastCommandService.setLastCommand(request.getChatId(), request.getText());
        }
    }
}