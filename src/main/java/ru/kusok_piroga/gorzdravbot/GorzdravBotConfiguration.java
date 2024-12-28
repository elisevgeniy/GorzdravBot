package ru.kusok_piroga.gorzdravbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.session.SessionProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kusok_piroga.gorzdravbot.bot.ModifiedTelegramBot;

import java.util.Collection;

@Configuration
public class GorzdravBotConfiguration {

    private static final String TELEGRAM_BOT = "TelegramBot";
    @Bean
    @Primary
    public TelegramBot telegramLongPollingBot(
            TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
            ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
            UpdateFilterProvider updateFilterProvider, TelegramMessageSource messageSource,
            TelegramClient telegramClient
    ) {
        if (StringUtils.isBlank(properties.getToken())) {
            throw new BeanCreationException(TELEGRAM_BOT,
                    "Consider specify drednote.telegram.token");
        }
        if (properties.getSession().getUpdateStrategy() == SessionProperties.UpdateStrategy.LONG_POLLING) {
            return new ModifiedTelegramBot(properties, updateHandlers, objectMapper,
                    exceptionHandler, updateFilterProvider, messageSource, telegramClient);
        } else {
            throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
        }
    }
}
