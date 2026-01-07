package ru.kusok_piroga.gorzdravbot.bot.services;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhiteListService implements ICommandService {
    @Value("${whitelist.enable}")
    Boolean enabled = false;

    @Value("${whitelist.ids}")
    Set<Long> accessedIds;

    public boolean checkAccess(Long id){
        return !enabled || accessedIds.contains(id);
    }

    @Override
    public TelegramResponse processCommand(UpdateRequest request) {
        log.error("Кто-то попытался получить доступ к боту. [id={}{}]", request.getChatId(), (request.getUser() != null) ? (
                "; name=" + request.getUser().getUserName()
                        + "(" + request.getUser().getFirstName() + " " + request.getUser().getLastName() + ")") : "");
        return new GenericTelegramResponse("""
                У Вас нет доступа к боту. По всем вопросам - к автору.
                """);
    }

    @Override
    public TelegramResponse processMessage(UpdateRequest request) {
        return null;
    }
}
