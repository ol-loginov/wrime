package wrime.tags;

import wrime.WrimeException;
import wrime.ops.Chain;
import wrime.ops.Operand;
import wrime.ops.Raw;
import wrime.scanner.WrimeScanner;
import wrime.util.ExpressionContextChild;
import wrime.util.ExpressionContextKeeper;
import wrime.util.TypeName;
import wrime.util.TypeWrap;

public class ForTagProcessor extends PathReceiver implements TagProcessor {
    private enum Status {
        NO_DECISION,
        WAIT_VAR,
        WAIT_ITERATOR,
        COMPLETE
    }

    private Status status = Status.NO_DECISION;

    private String varName = "";
    private TypeName varType;

    private Operand iterator;

    @Override
    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case NO_DECISION:
                status = Status.WAIT_VAR;
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
                TypeWrap iterableType = TypeWrap.create(iterator.getResult().getType());
                TypeWrap iteratorType = null;
                if (iterableType.isAssignableTo(Iterable.class)) {
                    iteratorType = TypeWrap.create(iterableType.getTypeParameterOf(Iterable.class, 0));
                } else if (iterableType.isArray()) {
                    iteratorType = TypeWrap.create(iterableType.getComponentType());
                } else {
                    error("call is not iterable");
                }

                chain = new Chain();
                chain.getOperands().add(new Raw(String.format("for(%s %s : ", iteratorType.getJavaSourceName(), varName)));
                chain.getOperands().add(iterator);
                chain.getOperands().add(new Raw(") {"));
                path.render(chain);

                ExpressionContextChild context = scope.openScope();
                context.addAttribute(ContinueTagFactory.SCOPE_ATTRIBUTE);
                context.addAttribute(BreakTagFactory.SCOPE_ATTRIBUTE);
                context.addVar(varName, new TypeName(iteratorType.getType()));

                break;
            case NO_DECISION:
                scope.closeScope();

                chain = new Chain();
                chain.getOperands().add(new Raw("}"));
                path.render(chain);

                break;
            default:
                error("${for(...)} is incomplete");
        }
    }


    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        switch (status) {
            case WAIT_VAR:
                varName += name;
                break;
            default:
                errorUnexpected(name);
        }
    }

    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        switch (status) {
            case WAIT_VAR:
                if (":".equals(delimiter)) {
                    status = Status.WAIT_ITERATOR;
                    path.push(new CallReceiver().setCloser(createCloser()), scope);
                    return;
                }
            default:
                errorUnexpected(delimiter);
        }
    }

    private CompleteCallback createCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                if (!last) {
                    error("only one expression allowed for iterator");
                }
                iterator = ((CallReceiver) child).getOperand();
                status = Status.COMPLETE;
            }
        };
    }
}
