package wrime.output;

import wrime.ast.Emitter;

import java.io.IOException;

public interface BodyWriter {
    BodyWriter append(Emitter emitter) throws IOException;

    BodyWriter append(CharSequence string) throws IOException;

    BodyWriter line(CharSequence string) throws IOException;

    BodyWriter nl() throws IOException;
}
