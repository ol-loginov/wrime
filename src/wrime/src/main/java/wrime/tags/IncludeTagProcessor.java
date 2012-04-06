package wrime.tags;

import wrime.ast.TagInclude;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;

public class IncludeTagProcessor implements TagProcessor {
    private final TagInclude tag;

    public IncludeTagProcessor(TagInclude tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, BodyWriter body) throws IOException {
        CallMatcher matcher = new CallMatcher(tag.getSource());
        matcher.matchTypes(scope);
        CallMatcher.requireReturnType(tag.getSource(), String.class, "should be String");

        String modelName = String.format("m$$%d$%d", tag.getLocation().getLine(), tag.getLocation().getColumn());

        body
                .append("this.$$include(")
                .append(tag.getSource())
                .append(",").append(modelName).append(");");
    }
}
