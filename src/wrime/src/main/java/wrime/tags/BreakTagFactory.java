package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class BreakTagFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "breakable";

    @Override
    public TagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new TagProcessor() {
        };
    }
    /*
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
}        */
}
