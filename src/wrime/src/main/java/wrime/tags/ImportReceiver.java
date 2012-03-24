package wrime.tags;

import wrime.ExpressionContextKeeper;
import wrime.WrimeException;

public class ImportReceiver extends PathReceiver {
    private String imports = "";

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        if (imports.length() == 0) {
            error("empty import value");
        }
        scope.addImport(imports);
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        imports += name;
    }

    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        if (".".equals(delimiter) || "*".equals(delimiter)) {
            imports += delimiter;
        } else {
            errorUnexpected(delimiter);
        }
    }
}
