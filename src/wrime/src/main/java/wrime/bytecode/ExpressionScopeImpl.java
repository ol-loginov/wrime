package wrime.bytecode;

import wrime.WrimeException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionScopeImpl implements ExpressionScope {
    private final ExpressionScope parentScope;
    private final Map<String, Type> variables = new HashMap<String, Type>();
    private final Set<String> attributes = new HashSet<String>();

    public ExpressionScopeImpl(ExpressionScope parentScope) {
        this.parentScope = parentScope;
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public ExpressionScope addAttribute(String attribute) {
        attributes.add(attribute);
        return this;
    }

    public void addVar(String name, Type classDef) throws WrimeException {
        if (hasVar(name)) {
            throw new WrimeException("Variable named " + name + " is already in scope", null);
        }
        variables.put(name, classDef);
    }

    @Override
    public Type getVarType(String name) {
        Type classDef = variables.get(name);
        if (classDef != null) {
            return classDef;
        }
        return parentScope != null ? parentScope.getVarType(name) : null;
    }

    @Override
    public boolean hasVar(String name) {
        return variables.get(name) != null || (parentScope != null && parentScope.hasVar(name));
    }
}
