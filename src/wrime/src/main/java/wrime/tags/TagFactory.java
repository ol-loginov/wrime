package wrime.tags;

import wrime.WrimeException;

public interface TagFactory {
    boolean supports(String name);

    PathReceiver createReceiver(String name) throws WrimeException;
}
