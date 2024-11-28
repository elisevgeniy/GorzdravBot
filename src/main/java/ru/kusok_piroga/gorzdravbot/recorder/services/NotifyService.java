package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.RecordCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.services.RawSendService;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.recorder.models.NotifyToChatData;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_UNIT;
import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_VALUE;
import static ru.kusok_piroga.gorzdravbot.utils.DateConverter.getPrintableAppointmentDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyService {
    private final RawSendService sendService;
    private final CallbackEncoder callbackEncoder;

    public boolean needNotify(TaskEntity task) {
        return task.getLastNotify() == null;
    }

    public boolean notifyToChat(TaskEntity task, List<AvailableAppointment> availableAppointments) {
        sendService.sendMessage(
                task.getDialogId(),
                "Выберите доступное время. Через %s %s будет автоматически произведена запись на %s"
                        .formatted(
                                DELAY_FOR_RECORD_VALUE,
                                (DELAY_FOR_RECORD_UNIT == Calendar.MINUTE) ? "мин." : "ед. времени",
                                getPrintableAppointmentDateTime(availableAppointments.get(0).visitStart())),
                availableAppointments.stream()
                        .map(availableAppointment ->
                                Map.of(
                                        getPrintableAppointmentDateTime(availableAppointment.visitStart()),
                                        callbackEncoder.encode(
                                                RecordCallbackUnit.FN_RECORD,
                                                new NotifyToChatData(task.getId(), availableAppointment.id())
                                        )
                                )
                        ).toList()
        );
        return true;
    }
}
