package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.WrongParamException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.WrongIdException;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

@Service
@RequiredArgsConstructor
public class TaskDeleteCommandService {

    private final TaskService service;

    public void deleteTask(String taskId) {
        try {
            service.deleteTask(taskId);
        } catch (WrongIdException e) {
            throw new WrongParamException();
        }
    }
}
