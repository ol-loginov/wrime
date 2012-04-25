package wrime.tags;

import wrime.output.BodyWriter;
import wrime.util.ExpressionContextKeeper;

import java.io.IOException;

public interface TagProcessor {
    void render(ExpressionContextKeeper context, BodyWriter body) throws IOException;
}
