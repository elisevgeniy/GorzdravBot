package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kusok_piroga.gorzdravbot.api.models.District;
import ru.kusok_piroga.gorzdravbot.api.models.Polyclinic;
import ru.kusok_piroga.gorzdravbot.api.services.ApiService;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskState;
import ru.kusok_piroga.gorzdravbot.bot.repositories.TaskRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService implements ICommandService {

    private final ApiService api;
    private final TaskRepository repository;

    private UpdateRequest request;

    @Override
    public TelegramResponse execute(UpdateRequest request) {
        this.request = request;

        Long dialogId = request.getChatId();

        Optional<TaskEntity> result = repository.findFirstByDialogIdAndStateIsNot(dialogId, TaskState.FINAL);

        if (result.isEmpty()) {

            TaskEntity task = new TaskEntity();
            task.setDialogId(dialogId);
            task.setState(TaskState.SET_DISTRICT);
            repository.save(task);

            StringBuilder answerText = new StringBuilder();
            answerText.append("Выберите район:\n");
            List<Map<String, String>> buttons = new ArrayList<>();

            for (District district : api.getDistricts()) {
                buttons.add(new HashMap<>());
                buttons.get(buttons.size()-1).put(district.getName(), district.getId().toString());
            }

            sendMessageWithInlineKeyboard(answerText.toString(), buttons);

            return null;
        } else {
            TaskEntity task = result.get();
            String message = request.getText();

            switch (task.getState()) {
                case SET_DISTRICT -> {
                    Integer districtId = Integer.parseInt(message);
                    task.setDistrictId(districtId);
                    task.setState(TaskState.SET_POLYCLINIC);
                    return printPolyclinics(districtId);
                }
            }

            repository.save(task);

            return new GenericTelegramResponse("");
        }

    }

    private GenericTelegramResponse printPolyclinics(Integer distrinctId) {
        StringBuilder answerText = new StringBuilder();
        answerText.append("Выберите мед. учреждение:\n");

        for (Polyclinic polyclinic : api.getPolyclinicsByDistrict(distrinctId)) {
            answerText.append("<code>%s</code>%n".formatted(polyclinic.lpuFullName()));
        }

        return new GenericTelegramResponse(answerText.toString());
    }

    private void sendMessageWithInlineKeyboard(String text, List<Map<String, String>> buttons) {

        SendMessage message = SendMessage
                .builder()
                .chatId(request.getChatId())
                .text(text)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(buttons.stream()
                                        .map(rowButtons -> new InlineKeyboardRow(
//                                        rowButtons.toArray(new String[0])
                                                rowButtons.entrySet().stream()
                                                        .map(button -> InlineKeyboardButton
                                                                .builder()
                                                                .text(button.getKey())
                                                                .callbackData(button.getValue())
                                                                .build()
                                                        ).toList()
                                        )).toList()
//                        .keyboardRow(
//                                new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Update message text")
//                                        .callbackData("update_msg_text")
//                                        .build()
//                                )
//                        )
//                        .build()
                        )
                        .build())
                .build();


        try {
            request.getAbsSender().execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
