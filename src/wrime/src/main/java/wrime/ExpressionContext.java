package wrime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionContext {
    private final ClassLoader classLoader;
    private final ExpressionContext parentContext;

    private Map<String, TypeName> localVariables = new HashMap<String, TypeName>();
    private Set<String> attributes = new HashSet<String>();

    public ExpressionContext(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.parentContext = null;
    }

    public ExpressionContext(ExpressionContext parentContext, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.parentContext = parentContext;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public void addVar(String name, TypeName classDef) throws WrimeException {
        if (hasVar(name)) {
            throw new WrimeException("Variable named " + name + " is already in scope", null);
        }
        localVariables.put(name, classDef);
    }

    public TypeName getVarType(String name) {
        TypeName classDef = localVariables.get(name);
        if (classDef != null) {
            return classDef;
        }
        return parentContext != null ? parentContext.getVarType(name) : null;
    }

    public boolean hasVar(String name) {
        return localVariables.get(name) != null || (parentContext != null && parentContext.hasVar(name));
    }
}
