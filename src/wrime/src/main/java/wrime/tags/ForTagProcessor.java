package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagFor;
import wrime.bytecode.ExpressionStack;
import wrime.output.BodyWriter;
import wrime.reflect.TypeConverter;
import wrime.reflect.TypeUtil;
import wrime.reflect.Types;

import java.io.IOException;
import java.lang.reflect.Type;

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

                Type iterableType = tag.getIterable().getReturnType();
                Type iteratorType;
                if (TypeConverter.isAssignable(Iterable.class, iterableType)) {
                    iteratorType = TypeUtil.getTypeParameterOf(iterableType, Iterable.class, 0);
                } else {
                    iteratorType = TypeUtil.getComponentType(iterableType);
                }

                body.append(String.format("for(%s %s : ", Types.getJavaSourceName(iteratorType), tag.getVariable().getText()))
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
