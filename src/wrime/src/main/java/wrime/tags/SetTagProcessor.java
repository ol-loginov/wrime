package wrime.tags;

import wrime.WrimeException;
import wrime.ops.Chain;
import wrime.ops.Operand;
import wrime.ops.Raw;
import wrime.util.ExpressionContextKeeper;
import wrime.util.TypeWrap;

import java.util.ArrayList;
import java.util.List;

public class SetTagProcessor extends PathReceiver implements TagProcessor {
    enum Status {
        WAIT_ASSIGNMENT,
        COMPLETE
    }

    static class VarDeclaration {
        private String name;
        private Operand source;
        private boolean declaration;
    }

    Status status;
    List<VarDeclaration> varList = new ArrayList<VarDeclaration>();

    @Override
    public String getHumanName() {
        return "SET definer";
    }

    @Override
    public void setup(ExpressionContextKeeper scope) throws WrimeException {
        super.setup(scope);
        consumeNextAssignment(scope);
    }

    private void consumeNextAssignment(ExpressionContextKeeper scope) throws WrimeException {
        status = Status.WAIT_ASSIGNMENT;
        path.push(new AssignReceiver().setCompleteCallback(createAssignCallback()), scope);
    }

    private CompleteCallback createAssignCallback() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                AssignReceiver assign = (AssignReceiver) child;
                path.remove(assign);
                status = Status.COMPLETE;
                addVariable(scope, assign.getAlias(), assign.getSource());

                if (!last) {
                    consumeNextAssignment(scope);
                }
            }
        };
    }

    private void addVariable(ExpressionContextKeeper scope, String alias, Operand source) throws WrimeException {
        VarDeclaration var = new VarDeclaration();
        var.name = alias;
        var.source = source;
        var.declaration = !scope.current().hasVar(var.name);
        varList.add(var);

        if (var.declaration) {
            scope.current().addVar(var.name, var.source.getResult());
        } else {
            //validate type of assignment
            TypeWrap varTypeInfo = TypeWrap.create(scope.current().getVarType(var.name).getType());
            if (!varTypeInfo.isAssignableFrom(var.source.getResult().getType())) {
                error("Value cannot be cast to variable '" + var.name + "'");
            }
        }
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        Chain chain = new Chain();
        for (VarDeclaration var : varList) {
            if (var.declaration) {
                chain.getOperands().add(new Raw(TypeWrap.create(var.source.getResult().getType()).getJavaSourceName()));
                chain.getOperands().add(new Raw(" "));
            }
            chain.getOperands().add(new Raw(String.format("%s = ", var.name)));
            chain.getOperands().add(var.source);
            chain.getOperands().add(new Raw(";\n"));
        }
        path.render(chain);
    }
};