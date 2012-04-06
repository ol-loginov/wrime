package wrime.util;

import wrime.WrimeException;

import java.util.*;

public class ExpressionContextChild implements ExpressionScope {
    private final ClassLoader classLoader;
    private final ExpressionScope parentScope;

    private Map<String, TypeName> localVariables = new HashMap<String, TypeName>();
    private Set<String> attributes = new HashSet<String>();

    public ExpressionContextChild(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.parentScope = null;
    }

    public ExpressionContextChild(ExpressionScope parentScope, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.parentScope = parentScope;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public Collection<String> getAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    public ExpressionContextChild addAttribute(String attribute) {
        attributes.add(attribute);
        return this;
    }

    public void addAttributeAll(Collection<String> attributeList) {
        attributes.addAll(attributeList);
    }

    public void addVar(String name, TypeName classDef) throws WrimeException {
        if (hasVar(name)) {
            throw new WrimeException("Variable named " + name + " is already in scope", null);
        }
        localVariables.put(name, classDef);
    }

    @Override
    public TypeName getVarType(String name) {
        TypeName classDef = localVariables.get(name);
        if (classDef != null) {
            return classDef;
        }
        return parentScope != null ? parentScope.getVarType(name) : null;
    }

    @Override
    public boolean hasVar(String name) {
        return localVariables.get(name) != null || (parentScope != null && parentScope.hasVar(name));
    }

    @Override
    public boolean hasFunctor(String name) {
        return parentScope != null && parentScope.hasFunctor(name);
    }

    @Override
    public TypeName getFunctorType(String name) {
        return parentScope != null ? parentScope.getFunctorType(name) : null;
    }
}
