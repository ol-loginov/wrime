package wrime.output;

import wrime.WrimeException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class WrimeWriter {
    private final Writer writer;
    private IncludeWriterListener $$includeWriterListener;

    private Map<String, Object> model;

    protected WrimeWriter(Writer writer) {
        this.writer = writer;
    }

    public void setIncludeWriterListener(IncludeWriterListener listener) {
        this.$$includeWriterListener = listener;
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

    protected void $$t(String text) throws IOException {
        if (text == null || text.length() == 0) {
            return;
        }
        writer.write(text);
    }

    protected void $$e(Object value) throws IOException {
        if (value == null) {
            return;
        }
        writer.write(value.toString());
    }

    protected void $$r(Object value) throws IOException {
        if (value == null) {
            return;
        }
        writer.write(value.toString());
    }

    protected boolean ifTrue(boolean bool) {
        return bool;
    }

    protected boolean ifTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    protected boolean ifTrue(Object bool) {
        return bool != null;
    }

    protected void include(String resource, Map<String, Object> model) {
        if (this.$$includeWriterListener == null) {
            throw new WrimeException("cannot handle include statement", null);
        }
        this.$$includeWriterListener.include(this, resource, model, writer);
    }
}
