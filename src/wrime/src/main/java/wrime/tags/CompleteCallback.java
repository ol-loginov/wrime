package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;

public interface CompleteCallback {
    void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException;
}
