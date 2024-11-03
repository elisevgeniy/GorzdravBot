package ru.kusok_piroga.gorzdravbot.callbacks;

public abstract class BaseCallbackChain implements ICallbackChainUnit {

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
