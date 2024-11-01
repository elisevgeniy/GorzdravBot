package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskDeleteService {

    private final TaskRepository repository;

    public void deleteTask(String taskId, UpdateRequest request) {
        repository.deleteById(Long.parseLong(taskId));

        try {
            request.getAbsSender().execute(DeleteMessage.builder()
                    .chatId(request.getChatId())
                    .messageId(request.getOrigin().getCallbackQuery().getMessage().getMessageId())
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTask(String taskId) {
        repository.deleteById(Long.parseLong(taskId));
    }
}
