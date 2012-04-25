package wrime.util;

import wrime.WrimeException;
import wrime.lang.TypeName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionChildScope implements ExpressionScope {
    private final ExpressionScope parentScope;
    private final Map<String, TypeName> variables = new HashMap<String, TypeName>();
    private final Set<String> attributes = new HashSet<String>();

    public ExpressionChildScope(ExpressionScope parentScope) {
        this.parentScope = parentScope;
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public ExpressionScope addAttribute(String attribute) {
        attributes.add(attribute);
        return this;
    }

    public void addVar(String name, TypeName classDef) throws WrimeException {
        if (hasVar(name)) {
            throw new WrimeException("Variable named " + name + " is already in scope", null);
        }
        variables.put(name, classDef);
    }

    @Override
    public TypeName getVarType(String name) {
        TypeName classDef = variables.get(name);
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
