package wrime.scanner;

import wrime.ScriptResource;
import wrime.WrimeException;

import java.io.IOException;
import java.io.Writer;

public class WrimeScannerDumper implements WrimeScanner.Receiver {
    private final Writer writer;

    public WrimeScannerDumper(Writer writer) {
        this.writer = writer;
    }

    private void appendQuietly(String text) {
        try {
            writer.append(text);
        } catch (IOException e) {
            // sorry for that
        }
    }

    @Override
    public void setLocation(String path, int line, int column) {
    }

    @Override
    public void command(String command) throws WrimeException {
        appendQuietly("[command " + command + "]");
    }

    @Override
    public void startResource(ScriptResource resource) {
        appendQuietly("[enter " + resource.getPath() + "]");
    }

    @Override
    public void finishResource() {
        appendQuietly("[leave]");
    }

    @Override
    public void text(String text) {
        appendQuietly("[write] " + text);
    }
}
