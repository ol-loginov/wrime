package wrime.tags;

import wrime.ast.TagImport;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;

public class ImportTagProcessor implements TagProcessor {
    private final TagImport tag;

    public ImportTagProcessor(TagImport tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, BodyWriter body) throws IOException {
        scope.addImport(tag.getJavaImport());
    }
}
