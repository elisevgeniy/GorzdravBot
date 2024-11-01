package ru.kusok_piroga.gorzdravbot.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.kusok_piroga.gorzdravbot.common.models.CallbackData;

import java.util.Optional;

@UtilityClass
public class CallbackEncoder {
    public static String encode (String functionName, String data){
        return new CallbackData(functionName, data).toString();
    }
    public static String encode (String functionName, Object data){
        return new CallbackData(functionName, data.toString()).toString();
    }
    public static Optional<CallbackData> decode (String data) {
        try {
            return Optional.of((new ObjectMapper()).readValue(data, CallbackData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
