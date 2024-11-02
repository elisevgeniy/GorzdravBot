package ru.kusok_piroga.gorzdravbot.bot.callbacks;

import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;

@Component
public class CallbackChain {

    private ICallbackChainUnit firstUnit = null;
    private ICallbackChainUnit lastUnit = null;


    public CallbackChain(@Autowired ApplicationContext context) {

        context.getBeansOfType(ICallbackChainUnit.class).forEach((s, callbackChainUnit) -> {
            if (lastUnit == null) {
                firstUnit = callbackChainUnit;
            } else {
                lastUnit.setNext(callbackChainUnit);
            }
            lastUnit = callbackChainUnit;
        });

        lastUnit.setNext(new FinalCallbackUnit());
    }

    public TelegramResponse run(CallbackData data) {
        if (firstUnit == null) throw new RuntimeException("No chain units was created");
        return firstUnit.execute(data);
    }
}
