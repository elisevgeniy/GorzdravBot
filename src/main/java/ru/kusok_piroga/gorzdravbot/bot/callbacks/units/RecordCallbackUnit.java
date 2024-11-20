package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.bot.responses.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.recorder.models.NotifyToChatData;
import ru.kusok_piroga.gorzdravbot.recorder.services.RecordService;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordCallbackUnit extends BaseCallbackUnit {

    private final RecordService recordService;
    private final TaskRepository taskRepository;

    public static final String FN_RECORD = "rec_rcrd";

    @Override
    public TelegramResponse execute(CallbackData data) {
        if (!checkAffiliation(data.fn())) {
            return getNext().execute(data);
        }

        log.info("Callback, fn = {}, data = {}", data.fn(), data.d());

        switch (data.fn()) {
            case FN_RECORD:
                Optional<NotifyToChatData> notifyToChatData = NotifyToChatData.parse(data.d());
                if (notifyToChatData.isEmpty()) {
                    log.error("Callback wrong format");
                    return new GenericTelegramResponse("Ошибка значения callback");
                }

                Optional<TaskEntity> task = taskRepository.findById(notifyToChatData.get().tsk());

                if (task.isEmpty()) {
                    log.error("Task {} not found", notifyToChatData.get().tsk());
                    return new GenericTelegramResponse("Ошибка значения task");
                }

                recordService.makeRecord(task.get(), notifyToChatData.get().app());
                return new DeleteMessageTelegramResponse();
            default:
                log.warn("Callback unknown fn");
                return new GenericTelegramResponse("Ошибка значения callback");
        }
    }

    private boolean checkAffiliation(String function) {
        switch (function) {
            case FN_RECORD:
                return true;
            default:
                return false;
        }
    }
}
