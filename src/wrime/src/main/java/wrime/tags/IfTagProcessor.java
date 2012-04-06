package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagIf;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;

public class IfTagProcessor implements TagProcessor {
    public static final String SCOPE_ATTRIBUTE = "ifable";

    private final TagIf tag;

    public IfTagProcessor(TagIf tag) {
        this.tag = tag;
    }

    private void requireIfScope(ExpressionContextRoot scope) {
        if (!scope.current().hasAttribute(SCOPE_ATTRIBUTE)) {
            throw new WrimeException("Current scope is not FOR", null);
        }
    }

    @Override
    public void render(ExpressionContextRoot scope, BodyWriter body) throws IOException {
        switch (tag.getMode()) {
            case OPEN:
                new CallMatcher(tag.getTest())
                        .matchTypes(scope.current())
                        .requireBooleanReturnType("needed for test expression");
                body.append("if (").append(tag.getTest()).line(") {");
                scope.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case ELIF:
                requireIfScope(scope);
                scope.closeScope();

                new CallMatcher(tag.getTest())
                        .matchTypes(scope.current())
                        .requireBooleanReturnType("needed for test expression");
                body.append("} else if (").append(tag.getTest()).line(") {");
                scope.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case ELSE:
                requireIfScope(scope);
                scope.closeScope();
                body.line("} else {");
                scope.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case CLOSE:
                requireIfScope(scope);
                scope.closeScope();
                body.line("}");
                break;
            default:
                throw new WrimeException("Invalid tag FOR mode", null);
        }
    }
}
