package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kusok_piroga.gorzdravbot.bot.repositories.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskDeleteService {

    private final TaskRepository repository;

    public void deleteTask(String taskId, UpdateRequest request){
        repository.deleteById(Long.parseLong(taskId));

        try {
            request.getAbsSender().execute(DeleteMessages.builder()
                    .chatId(request.getChatId())
                    .messageIds(List.of(
                            request.getOrigin().getCallbackQuery().getMessage().getMessageId(),
                            request.getOrigin().getCallbackQuery().getMessage().getMessageId() - 1
                    ))
                    .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
