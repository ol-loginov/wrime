package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;
import wrime.ops.Raw;

public class ForBreakFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "break".equals(name);
    }

    @Override
    public PathReceiver createReceiver(String name) {
        return new PathReceiver() {
            @Override
            public void complete(ExpressionContextKeeper scope) throws WrimeException {
                if (!scope.current().hasAttribute(ForFactory.LOOP_SCOPE)) {
                    error("You may use 'break' only inside");
                }
                path.render(new Raw("break;"));
            }
        };
    }
}
