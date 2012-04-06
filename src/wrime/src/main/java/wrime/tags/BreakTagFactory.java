package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;

public class BreakTagFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "breakable";

    @Override
    public TagProcessor createProcessor(final WrimeTag tag) throws WrimeException {
        return new TagProcessor() {
            @Override
            public void render(ExpressionContextRoot scope, BodyWriter body) throws IOException {
                if (!scope.inheritAttribute(SCOPE_ATTRIBUTE)) {
                    throw new WrimeException("You may use 'break' only inside breakable scope", null, tag.getLocation());
                }
                body.line("break;");
            }
        };
    }
}
