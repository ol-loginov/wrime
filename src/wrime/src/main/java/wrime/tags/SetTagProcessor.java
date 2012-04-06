package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagSet;
import wrime.output.BodyWriter;
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
        String varName = tag.getVariable().getText();

        CallMatcher matcher = new CallMatcher(tag.getValue());
        matcher.matchTypes(scope);
        CallMatcher.requireReturnType(tag.getValue(), "type needed for variable declaration");

        if (!scope.current().hasVar(varName)) {
            scope.current().addVar(varName, tag.getValue().getReturnType());

            body
                    .append(TypeWrap.create(tag.getValue().getReturnType().getType()).getJavaSourceName())
                    .append(" ");
        } else {
            //validate type of assignment
            TypeWrap varTypeInfo = TypeWrap.create(scope.current().getVarType(varName).getType());
            if (!varTypeInfo.isAssignableFrom(tag.getValue().getReturnType().getType())) {
                throw new WrimeException("Value cannot be cast to variable '" + varName + "'", null, tag.getLocation());
            }
        }

        body
                .append(String.format("%s = ", varName))
                .append(tag.getValue())
                .append(";")
                .nl();
    }
}