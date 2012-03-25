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

    public Operand getTest() {
        return test;
    }

    @Override
    public String getHumanName() {
        return "IF builder";
    }

    @Override
    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case WAIT_CONDITION:
                path.push(new CallReceiver().setCloser(createConditionCloser()), scope);
                break;
            default:
                errorUnexpected(WrimeScanner.OPEN_LIST_SYMBOL);
        }
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case COMPLETE:
                if (test.getResult().isVoid()) {
                    error("call is not conditional statement");
                }

                renderStatement(scope, new Chain());

                ExpressionContext context = scope.openScope();
                context.addAttribute(SCOPE_ATTRIBUTE);

                break;
            case WAIT_CONDITION:
                assertScopeType(this, scope);
                scope.closeScope();

                Chain chain = new Chain();
                chain.getOperands().add(new Raw("}"));
                path.render(chain);

                break;
            default:
                error("if-elif-else tag is incomplete");
        }
    }

    public static void assertScopeType(PathReceiver receiver, ExpressionContextKeeper scope) throws WrimeException {
        if (!scope.current().hasAttribute(SCOPE_ATTRIBUTE)) {
            receiver.error("current scope is not IF scope");
        }
    }

    protected void renderStatement(ExpressionContextKeeper scope, Chain chain) throws WrimeException {
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
