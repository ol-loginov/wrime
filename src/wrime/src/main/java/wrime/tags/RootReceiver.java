package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;

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
                PathReceiver receiver = factory.createReceiver(name);
                path.push(receiver);
                return;
            }
        }

        CallReceiver receiver = new CallReceiver();
        path.push(receiver);
        receiver.pushToken(scope, name);
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        //super-duper
    }
}
