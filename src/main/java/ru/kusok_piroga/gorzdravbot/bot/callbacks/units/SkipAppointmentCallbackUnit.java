package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.SkipAppointmentRepository;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.SkipAppointmentDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkipAppointmentCallbackUnit extends BaseCallbackUnit {

    private final TaskService taskService;
    private final SkipAppointmentRepository skipAppointmentRepository;

    public static final String FN_SKIP = "skip_appntmt";

    @Override
    public TelegramResponse execute(Long dialogId, CallbackData data) {
        if (!checkAffiliation(data.fn())) {
            return getNext().execute(dialogId, data);
        }

        log.info("Callback, fn = {}, data = {}", data.fn(), data.d());

        switch (data.fn()) {
            case FN_SKIP:
                Optional<SkipAppointmentDto> skipAppointmentDto = SkipAppointmentDto.parse(data.d());
                if (skipAppointmentDto.isEmpty()) {
                    log.error("Callback wrong format");
                    return new GenericTelegramResponse("Ошибка значения callback");
                }

                boolean result = taskService.skipAppointment(
                  skipAppointmentDto.get().taskId(),
                  skipAppointmentDto.get().appointmentId()
                );

                if (result){
                    return new GenericTelegramResponse("Номерок '%s' добавлен в список пропускаемых номерков"
                            .formatted(skipAppointmentDto.get().appointmentId()));
                } else {
                    log.error("Fail add '{}' to skip list for task '{}'",
                            skipAppointmentDto.get().taskId(),
                            skipAppointmentDto.get().appointmentId());
                    return new GenericTelegramResponse("Не удалось добавить '%s' в список пропускаемых номерков"
                            .formatted(skipAppointmentDto.get().appointmentId()));
                }
            default:
                log.warn("Callback unknown fn");
                return new GenericTelegramResponse("Ошибка значения callback");
        }
    }

    private boolean checkAffiliation(String function) {
        switch (function) {
            case FN_SKIP:
                return true;
            default:
                return false;
        }
    }
}
