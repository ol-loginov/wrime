package wrime;

import wrime.ast.ClassName;
import wrime.bytecode.ExpressionScope;
import wrime.bytecode.ExpressionStack;
import wrime.lang.TypeName;

import java.util.HashMap;
import java.util.Map;

public class ExpressionKeeperMock implements ExpressionStack {
    private ExpressionScopeMock current = new ExpressionScopeMock();
    private final Map<String, TypeName> functors = new HashMap<String, TypeName>();

    public Map<String, TypeName> getFunctors() {
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
    public Class findClass(ClassName className) {
        throw new IllegalStateException();
    }

    @Override
    public void addImport(String className) {
        throw new IllegalStateException();
    }

    @Override
    public void addParameter(String parameterName, Class parameterClass, String options) throws WrimeException {
        throw new IllegalStateException();
    }

    @Override
    public boolean inheritAttribute(String attribute) {
        throw new IllegalStateException();
    }

    @Override
    public TypeName getFunctorType(String functor) {
        return functors.get(functor);
    }
}
