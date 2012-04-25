package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;
import wrime.bytecode.ExpressionStack;
import wrime.output.BodyWriter;

import java.io.IOException;

public class ContinueTagFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "continuable";

    @Override
    public TagProcessor createProcessor(final WrimeTag tag) throws WrimeException {
        return new TagProcessor() {
            @Override
            public void render(ExpressionStack context, BodyWriter body) throws IOException {
                if (!context.inheritAttribute(SCOPE_ATTRIBUTE)) {
                    throw new WrimeException("You may use 'continue' only inside continuable block", null, tag.getLocation());
                }
                body.line("continue;");
            }
        };
    }
}
