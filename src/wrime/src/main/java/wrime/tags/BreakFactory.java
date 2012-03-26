package wrime.tags;

import wrime.WrimeException;
import wrime.ops.Raw;
import wrime.util.ExpressionContextKeeper;

public class BreakFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "breakable";

    @Override
    public boolean supports(String name) {
        return "break".equals(name);
    }

    @Override
    public PathReceiver createReceiver(String name) {
        return new PathReceiver() {
            @Override
            public void complete(ExpressionContextKeeper scope) throws WrimeException {
                if (!scope.inheritAttribute(SCOPE_ATTRIBUTE)) {
                    error("You may use 'break' only inside breakable scope");
                }
                path.render(new Raw("break;"));
            }
        };
    }
}
