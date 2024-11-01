package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.recorder.models.NotifyToChatData;

import java.util.Calendar;
import java.util.List;

import static ru.kusok_piroga.gorzdravbot.common.DateConverter.getPrintableAppointmentDateTime;
import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_UNIT;
import static ru.kusok_piroga.gorzdravbot.recorder.services.RecordService.DELAY_FOR_RECORD_VALUE;

@Service
@RequiredArgsConstructor
public class NotifyService {
    private final TelegramClient telegramClient;

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
                                                        new NotifyToChatData(task.getId(), availableAppointment.id())
                                                                .toString()
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
            return false;
        }
    }
}