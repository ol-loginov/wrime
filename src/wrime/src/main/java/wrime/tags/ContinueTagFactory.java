package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class ContinueTagFactory implements TagFactory {
    public static final String SCOPE_ATTRIBUTE = "continuable";

    /*
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
    */

    @Override
    public TagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new TagProcessor() {
        };
    }
}
