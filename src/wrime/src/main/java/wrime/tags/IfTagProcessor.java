package wrime.tags;

import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public class IfTagProcessor implements TagProcessor {
    public static final String SCOPE_ATTRIBUTE = "ifable";

    @Override
    public void render(ExpressionContextRoot scope, StringWriter body) throws IOException {
        throw new IllegalStateException();
    }
}
