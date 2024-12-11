package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.dto.SkipAppointmentDto;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.RecordCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.SkipAppointmentCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.bot.services.RawSendService;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.recorder.models.NotifyToChatData;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                appointmentText(availableAppointments),
                appointmentButtons(task, availableAppointments)
        );
        return true;
    }

    private String appointmentText(List<AvailableAppointment> availableAppointments){
        return "Выберите доступное время. Через %s %s будет автоматически произведена запись на %s. 'X' - пропустить"
                .formatted(
                        DELAY_FOR_RECORD_VALUE,
                        (DELAY_FOR_RECORD_UNIT == ChronoUnit.MINUTES) ? "мин." : "ед. времени",
                        getPrintableAppointmentDateTime(availableAppointments.get(0).visitStart()));
    }

    private List<Map<String, String>> appointmentButtons(TaskEntity task, List<AvailableAppointment> availableAppointments){
        List<Map<String, String>> buttons = new ArrayList<>();
        for (val availableAppointment: availableAppointments){
            buttons.add(new LinkedHashMap<>());
            buttons.get(buttons.size()-1).putAll(
                    appointmentRecordButton(task, availableAppointment)
            );
            buttons.get(buttons.size()-1).putAll(
                    appointmentSkipButton(task, availableAppointment)
            );
        }
        return buttons;
    }

    private Map<String, String> appointmentRecordButton(TaskEntity task, AvailableAppointment availableAppointment){
        return Map.of(
                getPrintableAppointmentDateTime(availableAppointment.visitStart()),
                callbackEncoder.encode(
                        RecordCallbackUnit.FN_RECORD,
                        new NotifyToChatData(task.getId(), availableAppointment.id())
                )
        );
    }
    private Map<String, String> appointmentSkipButton(TaskEntity task, AvailableAppointment availableAppointment){
        return Map.of(
                "X",
                callbackEncoder.encode(
                        SkipAppointmentCallbackUnit.FN_SKIP,
                        new SkipAppointmentDto(task.getId(), availableAppointment.id())
                )
        );
    }
}
