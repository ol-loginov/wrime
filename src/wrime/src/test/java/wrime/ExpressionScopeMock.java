package wrime;

import wrime.bytecode.ExpressionScope;
import wrime.lang.TypeInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ExpressionScopeMock implements ExpressionScope {
    private final Map<String, TypeInstance> variables = new HashMap<String, TypeInstance>();
    private final Set<String> attributes = new TreeSet<String>();

    public Map<String, TypeInstance> getVariables() {
        return variables;
    }

    @Override
    public TypeInstance getVarType(String name) {
        return variables.get(name);
    }

    @Override
    public boolean hasVar(String name) {
        return variables.containsKey(name);
    }

    @Override
    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    @Override
    public ExpressionScope addAttribute(String attribute) {
        attributes.add(attribute);
        return this;
    }

    @Override
    public void addVar(String variable, TypeInstance variableClass) {
        variables.put(variable, variableClass);
    }
}
