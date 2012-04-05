package wrime.tags;

import wrime.ast.EmitterWriter;
import wrime.ast.TagInclude;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public class IncludeTagProcessor implements TagProcessor {
    private final TagInclude tag;

    public IncludeTagProcessor(TagInclude tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, StringWriter body) throws IOException {
        CallMatcher matcher = new CallMatcher(tag.getSource());
        matcher.matchTypes(scope);
        CallMatcher.requireReturnType(tag.getSource(), String.class, "should be String");

        String modelName = String.format("m$$%d$%d", tag.getLocation().getLine(), tag.getLocation().getColumn());

        body.append("this.$$include(");
        new EmitterWriter(body).write(tag.getSource());
        body.append(",").append(modelName).append(");");
    }
}
