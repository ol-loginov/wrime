package wrime.tags;

import wrime.WrimeException;
import wrime.ops.EscapedRenderer;
import wrime.scanner.WrimeScanner;
import wrime.util.ExpressionContextKeeper;

import java.util.ArrayList;
import java.util.List;

public class RootReceiver extends PathReceiver {
    enum State {
        WAIT_EXPRESSION,
        RUN_EXPRESSION,
        COMPLETE
    }

    private final List<TagFactory> tagFactories;
    private final EscapedRenderer escapedRenderer;
    private State state = State.WAIT_EXPRESSION;

    public RootReceiver(List<TagFactory> factories, EscapedRenderer escapedRenderer) {
        this.tagFactories = new ArrayList<TagFactory>(factories);
        this.escapedRenderer = escapedRenderer;

        escapedRenderer.escapeBeforeWrite(true);
    }


    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        if (state == State.WAIT_EXPRESSION && WrimeScanner.RAW_SYMBOL.equals(delimiter)) {
            escapedRenderer.escapeBeforeWrite(false);
        } else {
            errorUnexpected(delimiter);
        }
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        state = State.RUN_EXPRESSION;
        for (TagFactory factory : tagFactories) {
            if (factory.supports(name)) {
                path.push(factory.createReceiver(name), scope);
                return;
            }
        }

        CallReceiver receiver = new CallReceiver();
        receiver.setShowUnknownTagError(true);
        receiver.setCloser(new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.render(((CallReceiver) child).getOperand());
            }
        });

        path.push(receiver, scope);
        receiver.pushToken(scope, name);
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        state = State.COMPLETE;
    }
}
