package wrime.output;

import wrime.WrimeException;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public abstract class WrimeWriter {
    private final Writer writer;
    protected final WrimeWriterComparisonMixin $$c = new WrimeWriterComparisonMixin();

    private IncludeCallback $$includeCallback;
    private Map<String, Object> model;

    protected WrimeWriter(Writer writer) {
        this.writer = writer;
    }

    public Map<String, Object> getCurrentModel() {
        return model;
    }

    public void setIncludeCallback(IncludeCallback listener) {
        this.$$includeCallback = listener;
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

    protected void $$include(String resource, Map<String, Object> model) {
        if (this.$$includeCallback == null) {
            throw new WrimeException("cannot handle include statement", null);
        }
        Map<String, Object> next = new HashMap<String, Object>(this.model);
        if (model != null && !model.isEmpty()) {
            next.putAll(model);
        }
        this.$$includeCallback.include(this, resource, next, writer);
    }
}
