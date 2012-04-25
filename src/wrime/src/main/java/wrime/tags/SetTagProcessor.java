package wrime.tags;

import wrime.WrimeException;
import wrime.ast.Emitter;
import wrime.ast.TagSet;
import wrime.bytecode.ExpressionStack;
import wrime.lang.TypeWrap;
import wrime.output.BodyWriter;

import java.io.IOException;

public class SetTagProcessor implements TagProcessor {
    private final TagSet tag;

    public SetTagProcessor(TagSet tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionStack context, BodyWriter body) throws IOException {
        for (TagSet.Variable var : tag.getVariables()) {
            assignVariable(context, body, var.variable, var.value);
        }
    }

    private void assignVariable(ExpressionStack context, BodyWriter body, String variable, Emitter value) throws IOException {

        new CallMatcher(value)
                .matchTypes(context)
                .requireReturnType("type needed for variable declaration");

        if (!context.current().hasVar(variable)) {
            context.current().addVar(variable, value.getReturnType());

            body
                    .append(TypeWrap.create(value.getReturnType().getType()).getJavaSourceName())
                    .append(" ");
        } else {
            //validate type of assignment
            TypeWrap varTypeInfo = TypeWrap.create(context.current().getVarType(variable).getType());
            if (!varTypeInfo.isAssignableFrom(value.getReturnType().getType())) {
                throw new WrimeException("Value cannot be cast to variable '" + variable + "'", null, tag.getLocation());
            }
        }

        body
                .append(String.format("%s = ", variable))
                .append(value)
                .append(";")
                .nl();
    }
}