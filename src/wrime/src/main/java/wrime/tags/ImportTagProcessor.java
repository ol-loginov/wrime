package wrime.tags;

import wrime.ast.TagImport;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextKeeper;

import java.io.IOException;

public class ImportTagProcessor implements TagProcessor {
    private final TagImport tag;

    public ImportTagProcessor(TagImport tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextKeeper context, BodyWriter body) throws IOException {
        context.addImport(tag.getJavaImport());
    }
}
