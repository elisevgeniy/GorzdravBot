package ru.kusok_piroga.gorzdravbot.callbacks;

import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.services.PatientDeleteService;
import ru.kusok_piroga.gorzdravbot.common.responses.DeleteMessageTelegramResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientCallbackChain extends BaseCallbackChain {

    private final PatientDeleteService patientDeleteService;

    public static final String FN_DELETE = "ptnt_del";

    @Override
    public TelegramResponse execute(CallbackData data) {
        if (!checkAffiliation(data.fn())){
            return getNext().execute(data);
        }

        log.info("Callback, fn = {}, data = {}", data.fn(), data.d());

        switch (data.fn()){
            case FN_DELETE:
                patientDeleteService.deletePatient(data.d());
                return new DeleteMessageTelegramResponse();
            default:
                log.info("Callback unknown fn");
                return new GenericTelegramResponse("Ошибка значения callback");
        }
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
