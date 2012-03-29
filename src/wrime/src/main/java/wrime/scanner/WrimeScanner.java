package wrime.scanner;

import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.WrimeException;

import java.util.Map;

public interface WrimeScanner {
    String OPEN_LIST_SYMBOL = "(";
    String CLOSE_LIST_SYMBOL = ")";
    String SPLIT_LIST_SYMBOL = ",";
    String EQUAL_SYMBOL = "=";
    String RAW_SYMBOL = "#";

    void configure(Map<WrimeEngine.Scanner, String> options);

    void scan(ScriptResource resource, Receiver receiver) throws WrimeException;

    public static interface Receiver {
        void startResource(ScriptResource resource) throws WrimeException;

        void finishResource() throws WrimeException;

        void text(String text) throws WrimeException;

        void command(String command) throws WrimeException;

        void setLocation(String path, int line, int column);
    }
}
