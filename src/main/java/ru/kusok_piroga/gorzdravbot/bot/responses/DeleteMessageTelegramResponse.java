package ru.kusok_piroga.gorzdravbot.bot.responses;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.AbstractTelegramResponse;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Delete a message with a callback button that was clicked
 */
public class DeleteMessageTelegramResponse extends AbstractTelegramResponse {

    @Override
    public void process(UpdateRequest request) throws TelegramApiException {

        if ((request.getMessage() == null || request.getMessage().getMessageId() == null)
        && request.getOrigin().getCallbackQuery() == null){
            return;
        }

        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .chatId(request.getChatId())
                .messageId(
                        (request.getOrigin().getCallbackQuery() == null) ?
                                request.getMessage().getMessageId()
                                :
                                request.getOrigin().getCallbackQuery().getMessage().getMessageId()
                )
                .build();

        request.getAbsSender().execute(deleteMessage);
    }
}
