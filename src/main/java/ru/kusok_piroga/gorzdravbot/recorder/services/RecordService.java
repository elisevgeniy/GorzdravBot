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
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.TaskCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.kusok_piroga.gorzdravbot.utils.DateConverter.getPrintableAppointmentDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordService {
    private final TelegramClient telegramClient;
    private final TaskRepository taskRepository;
    private final ApiService api;
    private final CallbackEncoder callbackEncoder;

    public static final int DELAY_FOR_RECORD_VALUE = 5;
    public static final int DELAY_FOR_RECORD_UNIT = Calendar.MINUTE;

    public boolean isTimeToRecord(TaskEntity task) {
        log.info("Task, id={}, isTimeToRecord check", task.getId());
        if (task.getLastNotify() == null) {
            log.info("Task, id={}, isTimeToRecord false, no notification before record", task.getId());
            return false;
        }

        Calendar notifyLowLimitCal = Calendar.getInstance();
        notifyLowLimitCal.setTime(new Date());
        notifyLowLimitCal.add(DELAY_FOR_RECORD_UNIT, -1 * DELAY_FOR_RECORD_VALUE);

        Date notifyLowLimit = notifyLowLimitCal.getTime();

        boolean result = task.getLastNotify().before(notifyLowLimit);

        log.info("Task, id={}, isTimeToRecord = {}", task.getId(), result);

        return result;
    }

    public boolean makeRecord(TaskEntity task, List<AvailableAppointment> availableAppointments){
        return makeRecord(task, availableAppointments.get(0));
    }

    public boolean makeRecord(TaskEntity task, AvailableAppointment availableAppointment){
        log.info("Task, id={}, making record", task.getId());
        if (Boolean.TRUE.equals(task.getCompleted())){
            log.info("Task, id={}, record fail, task already completed", task.getId());
            return false;
        }

        boolean isAppointmentCreated = api.createAppointment(
                task.getPolyclinicId(),
                availableAppointment.id(),
                task.getPatientEntity().getPatientId()
        );

        log.info("Task, id={}, record result = {}", task.getId(), isAppointmentCreated);

        if (isAppointmentCreated){
            task.setCompleted(true);
            task.setRecordedAppointmentId(availableAppointment.id());
            taskRepository.save(task);

            messageToChat(task, availableAppointment);
        }

        return isAppointmentCreated;
    }

    public boolean makeRecord(TaskEntity task, String appointmentId){
        log.info("Task, id={}, making record", task.getId());
        if (Boolean.TRUE.equals(task.getCompleted())){
            log.info("Task, id={}, record fail, task already completed", task.getId());
            return false;
        }

        boolean isAppointmentCreated = api.createAppointment(
                task.getPolyclinicId(),
                appointmentId,
                task.getPatientEntity().getPatientId()
        );

        log.info("Task, id={}, record result = {}", task.getId(), isAppointmentCreated);

        if (isAppointmentCreated){
            task.setCompleted(true);
            task.setRecordedAppointmentId(appointmentId);
            taskRepository.save(task);

            messageToChat(task, "Вы записаны. Номерок '%s'".formatted(appointmentId));
        }

        return isAppointmentCreated;
    }

    private boolean messageToChat(TaskEntity task, AvailableAppointment appointment) {
        return messageToChat(task,
                "Вы записаны в поликлинику %s к %s, %s на %s"
                        .formatted(
                                task.getPolyclinicId(),
                                task.getDoctorId(),
                                task.getSpecialityId(),
                                getPrintableAppointmentDateTime(appointment.visitStart())
                        )
        );
    }

    private boolean messageToChat(TaskEntity task, String message) {
        SendMessage telegramMessage = SendMessage
                .builder()
                .chatId(task.getDialogId())
                .text(message)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(
                                        InlineKeyboardButton
                                                .builder()
                                                .text("Отменить")
                                                .callbackData(getCancelCallbackData(task))
                                                .build()
                                )
                        ))
                        .build())
                .build();
        try {
            telegramClient.execute(telegramMessage);
            return true;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCancelCallbackData(TaskEntity task) {
        return callbackEncoder.encode(
                TaskCallbackUnit.FN_CANCEL,
                task.getId()
        );
    }
}
