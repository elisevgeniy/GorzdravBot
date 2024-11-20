package ru.kusok_piroga.gorzdravbot.bot.callbacks.units;

public abstract class BaseCallbackUnit implements ICallbackChainUnit {

    private ICallbackChainUnit next = null;

    @Override
    public void setNext(ICallbackChainUnit next) {
        this.next = next;
    }

    @Override
    public ICallbackChainUnit getNext() {
        return next;
    }
}
