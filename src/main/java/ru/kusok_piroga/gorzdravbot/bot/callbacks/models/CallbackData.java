package ru.kusok_piroga.gorzdravbot.bot.callbacks.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.utils.CallbackEncoder;

/**
 * Callback data model
 * @param fn function name
 * @param d data string
 * @see CallbackEncoder
 */
public record CallbackData(String fn, String d) {

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
