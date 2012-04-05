package wrime;

import wrime.util.ExpressionScope;
import wrime.util.TypeName;

import java.util.HashMap;
import java.util.Map;

public class ExpressionScopeMock implements ExpressionScope {
    private final Map<String, TypeName> variables = new HashMap<String, TypeName>();
    private final Map<String, TypeName> functors = new HashMap<String, TypeName>();

    public Map<String, TypeName> getVariables() {
        return variables;
    }

    public Map<String, TypeName> getFunctors() {
        return functors;
    }

    @Override
    public TypeName getVarType(String name) {
        return variables.get(name);
    }

    @Override
    public boolean hasVar(String name) {
        return variables.containsKey(name);
    }

    @Override
    public boolean hasFunctor(String name) {
        return functors.containsKey(name);
    }

    @Override
    public TypeName getFunctorType(String name) {
        return functors.get(name);
    }
}
