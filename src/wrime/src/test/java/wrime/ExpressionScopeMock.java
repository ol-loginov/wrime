package wrime;

import wrime.util.ExpressionScope;
import wrime.util.TypeName;

import java.util.HashMap;
import java.util.Map;

public class ExpressionScopeMock implements ExpressionScope {
    private final Map<String, TypeName> variables = new HashMap<String, TypeName>();

    public Map<String, TypeName> getVariables() {
        return variables;
    }

    @Override
    public TypeName getVarType(String name) {
        return hasVar(name) ? variables.get(name) : null;
    }

    @Override
    public boolean hasVar(String name) {
        return variables.containsKey(name);
    }
}
