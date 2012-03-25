package wrime.tags;

import wrime.*;
import wrime.ops.Chain;
import wrime.ops.Operand;
import wrime.ops.Raw;

public class IfReceiver extends PathReceiver {
    public static final String SCOPE_ATTRIBUTE = "ifable";

    enum Status {
        WAIT_CONDITION,
        COMPLETE
    }

    private Status status = Status.WAIT_CONDITION;

    private Operand test;

    @Override
    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case WAIT_CONDITION:
                path.push(new CallReceiver().setCloser(createConditionCloser()));
                break;
            default:
                errorUnexpected(WrimeScanner.OPEN_LIST_SYMBOL);
        }
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        Chain chain;
        switch (status) {
            case COMPLETE:
                if (test.getResult().isVoid()) {
                    error("call is not conditional statement");
                }

                chain = new Chain();
                chain.getOperands().add(new Raw("if("));

                TypeWrap testType = TypeWrap.create(test.getResult().getType());
                if (testType.isAssignableTo(Boolean.TYPE) || testType.isAssignableTo(Boolean.class)) {
                    chain.getOperands().add(new Raw("ifTrue("));
                } else {
                    chain.getOperands().add(new Raw("ifNotNull("));
                }

                chain.getOperands().add(test);
                chain.getOperands().add(new Raw(")) {"));
                path.render(chain);

                ExpressionContext context = scope.openScope();
                context.addAttribute(SCOPE_ATTRIBUTE);

                break;
            case WAIT_CONDITION:
                if (!scope.current().hasAttribute(SCOPE_ATTRIBUTE)) {
                    error("current scope is not IF scope");
                }
                scope.closeScope();

                chain = new Chain();
                chain.getOperands().add(new Raw("}"));
                path.render(chain);

                break;
            default:
                error("${if(...)} is incomplete");
        }
    }

    private CompleteCallback createConditionCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                if (!last) {
                    error("only one expression allowed");
                }
                setTestOperand(((CallReceiver) child).getOperand());
            }
        };
    }

    private void setTestOperand(Operand operand) throws WrimeException {
        if (operand == null) {
            error("test expression is required");
        }
        this.test = operand;
        this.status = Status.COMPLETE;
    }
}
