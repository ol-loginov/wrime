package wrime.tags;

import wrime.WrimeException;
import wrime.util.ExpressionContextKeeper;

public interface CompleteCallback {
    void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException;
}
