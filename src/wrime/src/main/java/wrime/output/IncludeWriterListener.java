package wrime.output;

import java.io.Writer;
import java.util.Map;

public interface IncludeWriterListener {
    void include(WrimeWriter caller, String resource, Map<String, Object> model, Writer out);
}
