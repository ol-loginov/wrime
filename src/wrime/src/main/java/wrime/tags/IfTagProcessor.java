package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagIf;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextKeeper;
import wrime.util.ExpressionScope;

import java.io.IOException;

public class IfTagProcessor implements TagProcessor {
    public static final String SCOPE_ATTRIBUTE = "ifable";

    private final TagIf tag;

    public IfTagProcessor(TagIf tag) {
        this.tag = tag;
    }

    private void requireIfScope(ExpressionScope scope) {
        if (!scope.hasAttribute(SCOPE_ATTRIBUTE)) {
            throw new WrimeException("Current scope is not FOR", null);
        }
    }

    @Override
    public void render(ExpressionContextKeeper context, BodyWriter body) throws IOException {
        switch (tag.getMode()) {
            case OPEN:
                new CallMatcher(tag.getTest())
                        .matchTypes(context)
                        .requireBooleanReturnType("needed for test expression");
                body.append("if (").append(tag.getTest()).line(") {");
                context.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case ELIF:
                requireIfScope(context.current());
                context.closeScope();

                new CallMatcher(tag.getTest())
                        .matchTypes(context)
                        .requireBooleanReturnType("needed for test expression");
                body.append("} else if (").append(tag.getTest()).line(") {");
                context.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case ELSE:
                requireIfScope(context.current());
                context.closeScope();
                body.line("} else {");
                context.openScope().addAttribute(SCOPE_ATTRIBUTE);
                break;
            case CLOSE:
                requireIfScope(context.current());
                context.closeScope();
                body.line("}");
                break;
            default:
                throw new WrimeException("Invalid tag FOR mode", null);
        }
    }
}
