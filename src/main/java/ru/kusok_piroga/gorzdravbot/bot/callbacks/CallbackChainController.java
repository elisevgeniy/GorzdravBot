package ru.kusok_piroga.gorzdravbot.bot.callbacks;

import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackData;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.FinalCallbackUnit;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.units.ICallbackChainUnit;

/**
 * <p>
 * Builds a chain of handlers and starts callback processing.<br>
 * Handlers must implement {@link ICallbackChainUnit}.<br>
 * Last handler is always {@link FinalCallbackUnit}.
 * </p>
 */
@Component
public class CallbackChainController {

    private ICallbackChainUnit firstUnit = null;
    private ICallbackChainUnit lastUnit = null;


    public CallbackChainController(@Autowired ApplicationContext context) {

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

    /** Starts callback processing
     * @param dialogId id of current telegram chat
     * @param data callback data
     * @return answer to telegram. See {@link TelegramResponse}
     */
    public TelegramResponse run(Long dialogId, CallbackData data) {
        if (firstUnit == null) throw new RuntimeException("No chain units was created");
        return firstUnit.execute(dialogId, data);
    }
}
