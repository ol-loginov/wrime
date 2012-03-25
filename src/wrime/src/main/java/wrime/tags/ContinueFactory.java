package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;
import wrime.ops.Raw;

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
                if (!scope.current().hasAttribute(SCOPE_ATTRIBUTE)) {
                    error("You may use 'continue' only inside continuable block");
                }
                path.render(new Raw("continue;"));
            }
        };
    }
}
