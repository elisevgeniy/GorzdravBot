package ru.kusok_piroga.gorzdravbot.bot.callbacks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackEntity;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.repositories.CallbackRepository;

import java.util.Optional;

/**
 * <p>
 *     Callback data encoder<br>
 *     It encodes the data with the string.<br>
 *     Long data is saved in the database, and the record id is encoded
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackEncoder {
    private final CallbackRepository repository;

    private static final String FN_DB_SAVED = "fn_db_saved";

    public String encode(String functionName, String data) {
        return new CallbackData(functionName, data).toString();
    }

    /**
     * <p>
     *     Encodes the data with the string.<br>
     *     Long data is saved in the database, and the record id is encoded.
     * </p>
     * @param functionName callback function name
     * @param data object for encode
     * @return encoded string or encoded DB record id (for big objects)
     */
    public String encode(String functionName, Object data) {
        String dataStr = data.toString();

        if (functionName.length() + dataStr.length() > 64 - 30) {
            return putToDB(functionName, data).toString();
        }

        return new CallbackData(functionName, data.toString()).toString();
    }

    /**
     * Just decode or get from DB (for big objects)
     * @param data encoded string
     * @return decoded object, empty if any error
     */
    public Optional<CallbackData> decode(String data) {
        try {
            CallbackData callbackData = new ObjectMapper().readValue(data, CallbackData.class);

            if (callbackData.fn().equals(FN_DB_SAVED)){
                return Optional.ofNullable(getFromDB(callbackData.d()));
            }

            return Optional.of((new ObjectMapper()).readValue(data, CallbackData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private CallbackData putToDB(String functionName, Object data) {
        CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.setFunction(functionName);
        callbackEntity.setData(data.toString());
        callbackEntity = repository.save(callbackEntity);
        return new CallbackData(FN_DB_SAVED, callbackEntity.getId().toString());
    }

    private CallbackData getFromDB(String id) {
        try {
            return getFromDB(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.error("Parse id fail from '{}'", id);
            return null;
        }
    }

    private CallbackData getFromDB(Long id) {
        Optional<CallbackEntity> callbackEntity = repository.findById(id);

        if (callbackEntity.isEmpty()) {
            log.error("Callback with id={} not found", id);
            return null;
        }

        return new CallbackData(callbackEntity.get().getFunction(), callbackEntity.get().getData());
    }
}
