package wrime.tags;

import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public interface TagProcessor {
    void render(ExpressionContextRoot scope, StringWriter body) throws IOException;
}
