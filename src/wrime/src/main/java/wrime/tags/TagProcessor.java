package wrime.tags;

import wrime.output.BodyWriter;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;

public interface TagProcessor {
    void render(ExpressionContextRoot scope, BodyWriter body) throws IOException;
}
