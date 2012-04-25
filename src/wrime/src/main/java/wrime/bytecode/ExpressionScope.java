package wrime.bytecode;

import wrime.lang.TypeInstance;

public interface ExpressionScope {
    boolean hasAttribute(String attribute);

    ExpressionScope addAttribute(String attribute);

    boolean hasVar(String name);

    void addVar(String variable, TypeInstance variableClass);

    TypeInstance getVarType(String name);
}
