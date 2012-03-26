package wrime.tags;

import wrime.WrimeException;
import wrime.util.ExpressionContextKeeper;

import java.util.ArrayList;
import java.util.List;

public class RootReceiver extends PathReceiver {
    private List<TagFactory> tagFactories;

    public RootReceiver(List<TagFactory> factories) {
        tagFactories = new ArrayList<TagFactory>(factories);
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
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
        //super-duper
    }
}
