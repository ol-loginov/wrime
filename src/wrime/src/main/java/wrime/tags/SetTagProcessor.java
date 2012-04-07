package wrime.tags;

import wrime.WrimeException;
import wrime.ast.Emitter;
import wrime.ast.TagSet;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextChild;
import wrime.util.ExpressionContextRoot;
import wrime.util.TypeWrap;

import java.io.IOException;

public class SetTagProcessor implements TagProcessor {
    private final TagSet tag;

    public SetTagProcessor(TagSet tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, BodyWriter body) throws IOException {
        for (TagSet.Variable var : tag.getVariables()) {
            assignVariable(scope.current(), body, var.variable, var.value);
        }
    }

    private void assignVariable(ExpressionContextChild scope, BodyWriter body, String variable, Emitter value) throws IOException {

        new CallMatcher(value)
                .matchTypes(scope)
                .requireReturnType("type needed for variable declaration");

        if (!scope.hasVar(variable)) {
            scope.addVar(variable, value.getReturnType());

            body
                    .append(TypeWrap.create(value.getReturnType().getType()).getJavaSourceName())
                    .append(" ");
        } else {
            //validate type of assignment
            TypeWrap varTypeInfo = TypeWrap.create(scope.getVarType(variable).getType());
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