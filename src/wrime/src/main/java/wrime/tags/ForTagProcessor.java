package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagFor;
import wrime.bytecode.ExpressionStack;
import wrime.lang.TypeInstance;
import wrime.output.BodyWriter;

import java.io.IOException;

public class ForTagProcessor implements TagProcessor {
    private final TagFor tag;

    public ForTagProcessor(TagFor tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionStack context, BodyWriter body) throws IOException {
        switch (tag.getMode()) {
            case OPEN:
                new CallMatcher(tag.getIterable())
                        .matchTypes(context);

                TypeInstance iterableType = tag.getIterable().getReturnType();
                TypeInstance iteratorType;
                if (iterableType.getDescriptor().isAssignableTo(Iterable.class)) {
                    iteratorType = new TypeInstance(iterableType.getDescriptor().getTypeParameterOf(Iterable.class, 0));
                } else if (iterableType.getDescriptor().isArray()) {
                    iteratorType = new TypeInstance(iterableType.getDescriptor().getComponentType());
                } else {
                    throw new WrimeException("iterable neither Array type nor Iterable", null, tag.getIterable().getLocation());
                }

                body.append(String.format("for(%s %s : ", iteratorType.toString(), tag.getVariable().getText()))
                        .append(tag.getIterable())
                        .line(") {");

                context.openScope()
                        .addAttribute(ContinueTagFactory.SCOPE_ATTRIBUTE)
                        .addAttribute(BreakTagFactory.SCOPE_ATTRIBUTE)
                        .addVar(tag.getVariable().getText(), iteratorType);

                break;
            case CLOSE:
                context.closeScope();
                body.line("}");
                break;
            default:
                throw new WrimeException("Wrong FOR tag mode", null);
        }
    }
}
