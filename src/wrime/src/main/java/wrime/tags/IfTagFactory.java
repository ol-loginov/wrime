package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class IfTagFactory implements TagFactory {
    @Override
    public IfTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new IfTagProcessor();
    }
    /*
    @Override
    public PathReceiver createReceiver(String name) throws WrimeException {
        if ("if".equals(name)) {
            return new IfReceiver();
        }
        if ("else".equals(name)) {
            return new ScopeSeparator.CopyAttributes() {
                @Override
                public String getHumanName() {
                    return "ELSE builder";
                }

                @Override
                protected void beforeScopeRemoved(ExpressionContextKeeper scope) throws WrimeException {
                    IfReceiver.assertScopeType(this, scope);
                    super.beforeScopeRemoved(scope);
                }

                @Override
                protected void afterScopeRemoved(ExpressionContextKeeper scope) throws WrimeException {
                    super.afterScopeRemoved(scope);
                    path.render(new Raw("} else {"));
                }
            };
        }
        if ("elif".equals(name)) {
            return new IfReceiver() {
                @Override
                public String getHumanName() {
                    return "ELIF builder";
                }

                @Override
                public void setup(ExpressionContextKeeper scope) throws WrimeException {
                    super.setup(scope);
                    IfReceiver.assertScopeType(this, scope);
                }

                @Override
                public void complete(ExpressionContextKeeper scope) throws WrimeException {
                    if (getTest() == null) {
                        error("no condition specified");
                    }
                    super.complete(scope);
                }

                @Override
                protected void renderStatement(ExpressionContextKeeper scope, Chain chain) throws WrimeException {
                    IfReceiver.assertScopeType(this, scope);
                    scope.closeScope();
                    chain.getOperands().add(new Raw("} else "));
                    super.renderStatement(scope, chain);
                }
            };
        }
        throw new WrimeException("Tag '" + name + "' is not supported", null);
    }  */
}
