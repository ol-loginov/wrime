package wrime.tags;

import wrime.ast.TagImport;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public class ImportTagProcessor implements TagProcessor {
    private final TagImport tag;

    public ImportTagProcessor(TagImport tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, StringWriter body) throws IOException {
        scope.addImport(tag.getPackagePath() + tag.getPackageTarget().getText());
    }
}
