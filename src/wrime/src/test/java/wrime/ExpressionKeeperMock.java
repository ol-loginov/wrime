package wrime;

import wrime.bytecode.ExpressionScope;
import wrime.bytecode.ExpressionStack;
import wrime.reflect.ClassName;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ExpressionKeeperMock implements ExpressionStack {
    private ExpressionScopeMock current = new ExpressionScopeMock();
    private final Map<String, Type> functors = new HashMap<String, Type>();

    public Map<String, Type> getFunctors() {
        return functors;
    }

    @Override
    public ExpressionScopeMock current() {
        return current;
    }

    @Override
    public ExpressionScope openScope() {
        throw new IllegalStateException();
    }

    @Override
    public ExpressionScope closeScope() {
        throw new IllegalStateException();
    }

    @Override
    public Type findClass(ClassName className) {
        throw new IllegalStateException();
    }

    @Override
    public void addImport(String className) {
        throw new IllegalStateException();
    }

    @Override
    public void addParameter(String parameterName, Type parameterClass, String options) throws WrimeException {
        throw new IllegalStateException();
    }

    @Override
    public boolean inheritAttribute(String attribute) {
        throw new IllegalStateException();
    }

    @Override
    public Type getFunctorType(String functor) {
        return functors.get(functor);
    }
}
