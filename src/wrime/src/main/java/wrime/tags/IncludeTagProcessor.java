package wrime.tags;

import wrime.ast.Assignment;
import wrime.ast.TagInclude;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextKeeper;

import java.io.IOException;

public class IncludeTagProcessor implements TagProcessor {
    private final TagInclude tag;

    public IncludeTagProcessor(TagInclude tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextKeeper context, BodyWriter body) throws IOException {
        new CallMatcher(tag.getSource())
                .matchTypes(context);
        CallMatcher.requireReturnType(tag.getSource(), String.class, "should be String");

        String modelName;
        if (tag.getArguments() == null || tag.getArguments().size() == 0) {
            modelName = "null";
        } else {
            modelName = String.format("$includeAt$%d$%d", tag.getLocation().getLine(), tag.getLocation().getColumn());

            body.line(String.format("Map<String, Object> %s = new TreeMap<String, Object>();", modelName));

            for (Assignment assignment : tag.getArguments()) {
                body.append(String.format("%s.put(\"%s\", ", modelName, assignment.getVar().getText()));
                if (assignment.getEmitter() == null) {
                    // we include local variable
                    body.append(String.format("%s", assignment.getVar().getText()));
                } else {
                    // we include function call
                    body.append(assignment.getEmitter());
                }
                body.line(");");
            }
        }

        body
                .append("this.$$include(")
                .append(tag.getSource())
                .append(", ").append(modelName).line(");");
    }
}
