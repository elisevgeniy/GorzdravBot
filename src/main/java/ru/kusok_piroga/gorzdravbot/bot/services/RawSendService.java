package ru.kusok_piroga.gorzdravbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kusok_piroga.gorzdravbot.bot.exceptions.RawSendException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawSendService {
    private final TelegramClient telegramClient;

    public void sendMessage(Long dialogId, String text, List<Map<String, String>> buttons){
        SendMessage.SendMessageBuilder<?, ?> sendMessageBuilder = SendMessage.builder()
                .chatId(dialogId)
                .text(text);
        if (buttons != null && !buttons.isEmpty()) {
            sendMessageBuilder = sendMessageBuilder.replyMarkup(InlineKeyboardMarkup
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
                    .build());
        }
        SendMessage message = sendMessageBuilder.build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Send message error", e);
            throw new RawSendException();
        }
    }

    public void sendMessage(Long dialogId, String text){
        sendMessage(dialogId, text, null);
    }
}
