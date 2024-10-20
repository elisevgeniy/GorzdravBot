package ru.kusok_piroga.gorzdravbot.common;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.AbstractTelegramResponse;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class InlineButtonTelegramResponse extends AbstractTelegramResponse {
    private final String messageText;
    private final List<Map<String, String>> buttons;

    /**
     * @param text    message text
     * @param buttons list = rows, map = button title -> button callbackValue
     */
    public InlineButtonTelegramResponse(@NonNull String text, @NonNull List<Map<String, String>> buttons) {
        messageText = text;
        this.buttons = buttons;
    }

    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        SendMessage message = SendMessage
                .builder()
                .chatId(request.getChatId())
                .text(messageText)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(buttons.stream()
                                .map(rowButtons -> new InlineKeyboardRow(
                                        rowButtons.entrySet().stream()
                                                .map(button -> InlineKeyboardButton
                                                        .builder()
                                                        .text(button.getKey())
                                                        .callbackData(button.getValue())
                                                        .build()
                                                ).toList()
                                )).toList()
                        )
                        .build())
                .build();


        request.getAbsSender().execute(message);
    }
}
