package wrime.tags;

import wrime.bytecode.ExpressionStack;
import wrime.output.BodyWriter;

import java.io.IOException;

public interface TagProcessor {
    void render(ExpressionStack context, BodyWriter body) throws IOException;
}
