package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.TypeName;
import wrime.TypeUtil;
import wrime.WrimeException;
import wrime.ops.*;

/**
 * Accepts and validate syntax like "field.method().foo"
 */
public class CallReceiver extends PathReceiver {
    private enum Expect {
        NONE,
        INVOKER
    }

    private CompleteCallback closer;
    private Operand operand;
    private Expect expect = Expect.NONE;

    public Operand getOperand() {
        return operand;
    }

    public CallReceiver setCloser(CompleteCallback closer) {
        this.closer = closer;
        return this;
    }

    @Override
    public String getHumanName() {
        return "Expression analyser";
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        path.render(operand);
    }

    @Override
    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        if (operand instanceof Invoker) {
            path.push(new CallReceiver().setCloser(createCloser()), scope);
        } else {
            error("expected at function point only");
        }
    }

    private CompleteCallback createCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                addOperand(((CallReceiver) child).getOperand());
                if (!last) {
                    path.push(new CallReceiver().setCloser(this), scope);
                } else {
                    resolveInvoker(scope);
                }
            }
        };
    }

    @Override
    public void closeList(ExpressionContextKeeper scope) throws WrimeException {
        if (closer != null) {
            closer.complete(this, scope, true);
        } else {
            error("unexpected list closure");
        }
    }

    private void resolveInvoker(ExpressionContextKeeper scope) throws WrimeException {
        if (!(operand instanceof Invoker)) {
            error("operand supposed to be at function call only");
        }

        Invoker invoker = (Invoker) operand;
        TypeName[] parameterTypes = new TypeName[invoker.getParameters().size()];
        for (int i = 0; i < parameterTypes.length; ++i) {
            parameterTypes[i] = invoker.getParameters().get(i).getResult();
        }
        Invoker confirmation = TypeUtil.findInvoker(invoker.getInvocable().getResult(), invoker.getMethodName(), parameterTypes);
        if (confirmation == null) {
            error("cannot find suitable method with name '" + invoker.getMethodName() + "'");
        }
        invoker.setMethod(confirmation.getMethod());
        invoker.setResult(confirmation.getResult());
    }

    private void addOperand(Operand argument) throws WrimeException {
        if (argument == null) {
            return;
        }
        if (operand instanceof Invoker) {
            ((Invoker) operand).getParameters().add(argument);
        } else {
            error("previous operand is not invocable");
        }
    }

    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        if (",".equals(delimiter)) {
            if (closer != null) {
                closer.complete(this, scope, false);
                return;
            }
        } else if (".".equals(delimiter) || ":".equals(delimiter)) {
            if (operand == null || operand.getResult().isVoid()) {
                error("no invocable at the point");
            }
            expect = Expect.INVOKER;
            return;
        }
        errorUnexpected(delimiter);
    }

    @Override
    public void pushLiteral(ExpressionContextKeeper scope, String literal) throws WrimeException {
        if (operand == null) {
            operand = new Literal(literal);
        } else {
            error("literal is not expected in this point");
        }
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        if (operand == null) {
            if (scope.current().getVarType(name) != null) {
                Variable getter = new Variable();
                getter.setVar(name);
                getter.setResult(scope.current().getVarType(name));
                operand = getter;
            } else if (scope.findFunctorType(name) != null) {
                Functor functor = new Functor();
                functor.setName(name);
                functor.setResult(scope.findFunctorType(name));
                operand = functor;
            } else {
                if (path.depth() > 2) {
                    error("unknown variable or functor '" + name + "'");
                } else {
                    error("unknown tag, variable or functor '" + name + "'");
                }
            }
        } else if (expect == Expect.INVOKER) {
            Operand invoker = TypeUtil.findAnyInvokerOrGetter(operand.getResult(), name);
            if (invoker instanceof Getter) {
                ((Getter) invoker).setInvocable(operand);
            } else if (invoker instanceof Invoker) {
                ((Invoker) invoker).setInvocable(operand);
            } else {
                error("unknown function or getter for '" + name + "'");
            }
            operand = invoker;
        } else {
            error("unexpected token");
        }
    }
}
