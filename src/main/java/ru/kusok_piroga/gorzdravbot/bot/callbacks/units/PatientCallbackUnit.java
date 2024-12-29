package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.exceptions.WrongCallbackDataException;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.responses.DeleteMessageTelegramResponse;
import ru.kusok_piroga.gorzdravbot.producer.services.PatientService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientCallbackUnit extends BaseCallbackUnit {

    private final PatientService patientService;

    public static final String FN_DELETE = "ptnt_del";

    @Override
    public TelegramResponse execute(Long dialogId, CallbackData data) {
        if (!checkAffiliation(data.fn())){
            return getNext().execute(dialogId, data);
        }

        log.info("Callback, fn = {}, data = {}", data.fn(), data.d());

        switch (data.fn()){
            case FN_DELETE:
                try {
                    patientService.deletePatient(data.d());
                } catch (Exception e){
                    throw new WrongCallbackDataException();
                }
                return new DeleteMessageTelegramResponse();
        }

        return null;
    }

    private boolean checkAffiliation(String function){
        switch (function){
            case FN_DELETE:
                return true;
            default:
                return false;
        }
    }
}
