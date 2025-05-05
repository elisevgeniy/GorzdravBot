package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.Commands;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.DateFormatException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.TimeConsistencyException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.TimeFormatException;
import ru.kusok_piroga.gorzdravbot.producer.exceptions.WrongReferralException;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;
import ru.kusok_piroga.gorzdravbot.producer.services.TaskService;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReferralCreateCommandService implements ICommandService {

    private final ApiService api;
    private final TaskService taskService;
    private final PatientService patientService;
    private final PatientListCommandService patientListCommandService;

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {

        long dialogId = request.getChatId();

        return new GenericTelegramResponse("Введите номер направления и фамилию пациента, например \"123456 Иванов\"");
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {

        String message = request.getText();

        if (Pattern.compile("\\d+ [а-яА-Я]+")
                .matcher(message)
                .matches())
        {
            String[] parts = message.split(" ");

            try {
                if (taskService.createTaskByReferral(
                        request.getChatId(),
                        parts[0],
                        parts[1]
                        )
                ) {
                    return printTimeLimits();
                }
            } catch (WrongReferralException e) {
                return new GenericTelegramResponse("Неправильный номер направления или Фамилия");
            }
        } else {

            TaskEntity task;

            try {
                task = taskService.getUnsetupedTaskByDialog(request.getChatId());
            } catch (NoSuchElementException e){
                return new GenericTelegramResponse("Начать создание задачи по направлению можно помощью " + Commands.COMMAND_ADD_REFERRAL);
            }

            try {
                taskService.fillTaskFields(task, request.getText());

            } catch (TimeFormatException e) {
                throw new ru.kusok_piroga.gorzdravbot.bot.exceptions.TimeFormatException();
            } catch (TimeConsistencyException e) {
                return new GenericTelegramResponse("Неверный диапазон времени, попробуйте снова с нижней границы");
            } catch (DateFormatException e) {
                throw new ru.kusok_piroga.gorzdravbot.bot.exceptions.DateFormatException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return switch (task.getState()) {
                case INIT -> null;
                case SET_TIME_LIMITS -> printTimeLimits();
                case SET_DATE_LIMITS -> printDateLimit();
                case SETUPED ->
                        new GenericTelegramResponse("Задача создана. Список задач: " + Commands.COMMAND_LIST_TASK);
                default -> null;
            };
        }
        return null;
    }

    private TelegramResponse printTimeLimits() {
        String answerText = """
                Пришлите одним сообщением на разных строках:
                1. Диапазон времени, в котором искать номерки в формате
                чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию 00:00 - 23:59)
                2. Диапазон времени, в котором НЕ искать номерки в формате
                !чч:мм - чч:мм, чч:мм - чч:мм (по умолчанию не будет установлен)
                Для пропуска настройки пришлите "дальше" """;
        return new GenericTelegramResponse(answerText);
    }

    private TelegramResponse printDateLimit() {
        String answerText = """
                Пришлите дату до которой искать номерки в формате
                дд.мм.гггг
                или диапазон дат, в котором искать номерки в формате
                дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг
                или диапазон дат, в котором искать и НЕ номерки в формате
                дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг !дд.мм.гггг - дд.мм.гггг, дд.мм.гггг - дд.мм.гггг""";
        return new GenericTelegramResponse(answerText);
    }
}
