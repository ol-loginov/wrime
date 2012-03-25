package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;
import wrime.WrimeScanner;
import wrime.ops.Operand;
import wrime.ops.Variable;

/**
 * Accepts and validate syntax like "var_name : field.method().foo"
 */
public class AssignReceiver extends PathReceiver {
    enum Status {
        WAIT_VAR,
        WAIT_SPLITTER,
        WAIT_CALL,
        COMPLETE
    }

    private Status status = Status.WAIT_VAR;

    private String alias;
    private Operand source;

    private ReceiverCallback aliasValidator;
    private CompleteCallback completeCallback;

    public String getAlias() {
        return alias;
    }

    public Operand getSource() {
        return source;
    }

    public AssignReceiver setAliasValidator(ReceiverCallback aliasValidator) {
        this.aliasValidator = aliasValidator;
        return this;
    }

    public AssignReceiver setCompleteCallback(CompleteCallback callback) {
        this.completeCallback = callback;
        return this;
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        switch (status) {
            case WAIT_VAR:
                status = Status.WAIT_SPLITTER;
                alias = name;
                validateAlias();
                break;
            default:
                errorUnexpected(name);
        }
    }

    private void validateAlias() {
        if (aliasValidator == null) {
            return;
        }
        aliasValidator.on(this);
    }

    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        switch (status) {
            case WAIT_SPLITTER:
                if (WrimeScanner.EQUAL_SYMBOL.equals(delimiter)) {
                    status = Status.WAIT_CALL;
                    path.push(new CallReceiver().setCloser(createCloser()), scope);
                } else if (WrimeScanner.SPLIT_LIST_SYMBOL.equals(delimiter)) {
                    setSourceFromAlias(scope);
                    markComplete(scope, false);
                } else {
                    errorUnexpected(delimiter);
                }
                break;
            default:
                errorUnexpected(delimiter);
        }
    }

    private void setSourceFromAlias(ExpressionContextKeeper scope) throws WrimeException {
        if (scope.current().getVarType(alias) == null) {
            error(String.format("'%s' is definitely not a value", alias));
        }

        Variable getter = new Variable();
        getter.setVar(alias);
        getter.setResult(scope.current().getVarType(alias));
        this.source = getter;
    }

    private CompleteCallback createCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                setSource(((CallReceiver) child).getOperand());
                markComplete(scope, last);
            }
        };
    }

    private void setSource(Operand operand) throws WrimeException {
        if (operand == null) {
            error("got no expression here");
        }
        source = operand;
    }

    private void markComplete(ExpressionContextKeeper scope, boolean last) throws WrimeException {
        status = Status.COMPLETE;
        if (completeCallback != null) {
            completeCallback.complete(this, scope, last);
        }
    }
}
