package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.RecordCallbackUnit;
import ru.kusok_piroga.gorzdravbot.recorder.models.NotifyToChatData;

import java.util.Calendar;
import java.util.List;

import static ru.kusok_piroga.gorzdravbot.utils.DateConverter.getPrintableAppointmentDateTime;
import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_UNIT;
import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyService {
    private final TelegramClient telegramClient;
    private final CallbackEncoder callbackEncoder;

    public boolean needNotify(TaskEntity task) {
        return task.getLastNotify() == null;
    }

    public boolean notifyToChat(TaskEntity task, List<AvailableAppointment> availableAppointments) {
        SendMessage message = SendMessage
                .builder()
                .chatId(task.getDialogId())
                .text("Выберите доступное время. Через %s %s будет автоматически произведена запись на %s"
                        .formatted(
                                DELAY_FOR_RECORD_VALUE,
                                (DELAY_FOR_RECORD_UNIT == Calendar.MINUTE) ? "мин." : "ед. времени",
                                getPrintableAppointmentDateTime(availableAppointments.get(0).visitStart())))
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(availableAppointments.stream()
                                .map(availableAppointment -> new InlineKeyboardRow(
                                        InlineKeyboardButton
                                                .builder()
                                                .text(getPrintableAppointmentDateTime(availableAppointment.visitStart()))
                                                .callbackData(
                                                        callbackEncoder.encode(
                                                                RecordCallbackUnit.FN_RECORD,
                                                                new NotifyToChatData(task.getId(), availableAppointment.id())
                                                        )
                                                )
                                                .build()
                                )).toList()
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Send message error", e);
            return false;
        }
    }
}
