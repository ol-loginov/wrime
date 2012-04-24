package wrime.output;

import java.io.Writer;
import java.util.Map;

public interface IncludeCallback {
    void include(WrimeWriter caller, String resource, Map<String, Object> model, Writer out);
}
