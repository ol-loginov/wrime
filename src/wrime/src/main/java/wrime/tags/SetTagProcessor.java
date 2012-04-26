package wrime.tags;

import wrime.WrimeException;
import wrime.ast.Emitter;
import wrime.ast.TagSet;
import wrime.bytecode.ExpressionStack;
import wrime.lang.TypeDef;
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
                    .append(value.getReturnType().getJavaSourceName())
                    .append(" ");
        } else {
            //validate type of assignment
            TypeDef varTypeInfo = context.current().getVarType(variable);
            if (!varTypeInfo.isAssignableFrom(value.getReturnType())) {
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