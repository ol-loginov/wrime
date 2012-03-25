package wrime.tags;

import wrime.WrimeException;

public class SetFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "set".equals(name);
    }

    @Override
    public SetReceiver createReceiver(String name) throws WrimeException {
        return new SetReceiver();
    }
}
