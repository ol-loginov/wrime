package wrime.tags;

import wrime.ast.TagFor;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public class ForTagProcessor implements TagProcessor {
    private final TagFor tag;

    public ForTagProcessor(TagFor tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, StringWriter body) throws IOException {
        throw new IllegalStateException();
    }
}
