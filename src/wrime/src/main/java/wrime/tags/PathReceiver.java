package wrime.tags;

import wrime.WrimeException;
import wrime.util.ExpressionContextKeeper;

public abstract class PathReceiver {
    protected PathContext path;

    void setPath(PathContext path) {
        this.path = path;
    }

    public void setup(ExpressionContextKeeper scope) throws WrimeException {
    }

    public void errorUnexpected(String token) throws WrimeException {
        throw new WrimeException(getClass().getSimpleName() + " reports about unexpected token '" + token + "'", null);
    }

    public String getHumanName() {
        return getClass().getName();
    }

    public void error(String text) throws WrimeException {
        throw new WrimeException(getHumanName() + " reports an error: " + text, null);
    }

    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        error("unexpected list");
    }

    public void closeList(ExpressionContextKeeper scope) throws WrimeException {
        error("unexpected list closure");
    }

    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        error("unexpected token");
    }

    public void pushLiteral(ExpressionContextKeeper scope, String literal) throws WrimeException {
        error("unexpected literal");
    }

    public void nextListItem(ExpressionContextKeeper scope) throws WrimeException {
        error("unexpected list sequence");
    }

    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        error("unexpected delimiter '" + delimiter + "'");
    }

    /**
     * Should be invoked only once
     *
     * @param scope current variables scope
     * @throws WrimeException in any error
     */
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        error("unexpected expression end");
    }
}
