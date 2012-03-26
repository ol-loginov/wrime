package wrime.output;

import wrime.WrimeException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class WrimeWriter {
    private final Writer writer;

    private Map<String, Object> model;

    protected WrimeWriter(Writer writer) {
        this.writer = writer;
    }

    public void render(Map<String, Object> model) throws WrimeException {
        if (model != null) {
            assignFields(model);
        }
        try {
            renderContent();
        } catch (Exception e) {
            throw new WrimeException("render error", e);
        } finally {
            clear();
        }
    }

    protected abstract void renderContent() throws Exception;

    protected void clear() {
        this.model = null;
    }

    protected void assignFields(Map<String, Object> model) {
        this.model = model;
    }

    protected void wt(String text) throws IOException {
        if (text == null || text.length() == 0) {
            return;
        }
        writer.write(text);
    }

    protected void we(Object value) throws IOException {
        if (value == null) {
            return;
        }
        writer.write(value.toString());
    }

    protected void wr(Object value) throws IOException {
        if (value == null) {
            return;
        }
        writer.write(value.toString());
    }
}
