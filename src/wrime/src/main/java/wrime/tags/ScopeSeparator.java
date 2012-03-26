package wrime.tags;

import wrime.WrimeException;
import wrime.util.ExpressionContextKeeper;

import java.util.Collection;

public class ScopeSeparator extends PathReceiver {
    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        beforeScopeRemoved(scope);
        scope.closeScope();
        afterScopeRemoved(scope);
        scope.openScope();
        afterScopeAdded(scope);
    }

    protected void afterScopeAdded(ExpressionContextKeeper scope) throws WrimeException {
    }

    protected void afterScopeRemoved(ExpressionContextKeeper scope) throws WrimeException {
    }

    protected void beforeScopeRemoved(ExpressionContextKeeper scope) throws WrimeException {
    }

    public static class CopyAttributes extends ScopeSeparator {
        Collection<String> attributes;

        @Override
        protected void beforeScopeRemoved(ExpressionContextKeeper scope) throws WrimeException {
            super.beforeScopeRemoved(scope);
            attributes = scope.current().getAttributes();
        }

        @Override
        protected void afterScopeAdded(ExpressionContextKeeper scope) throws WrimeException {
            super.afterScopeAdded(scope);
            if (attributes != null) {
                scope.current().addAttributeAll(attributes);
            }
        }
    }
}
