package wrime.tags;

import wrime.WrimeException;
import wrime.ops.Raw;
import wrime.util.ExpressionContextKeeper;

public class ContinueFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "continuable";

    @Override
    public boolean supports(String name) {
        return "continue".equals(name);
    }

    @Override
    public PathReceiver createReceiver(String name) {
        return new PathReceiver() {
            @Override
            public void complete(ExpressionContextKeeper scope) throws WrimeException {
                if (!scope.inheritAttribute(SCOPE_ATTRIBUTE)) {
                    error("You may use 'continue' only inside continuable block");
                }
                path.render(new Raw("continue;"));
            }
        };
    }
}
