package wrime.bytecode;

import wrime.lang.TypeName;

public interface ExpressionScope {
    boolean hasAttribute(String attribute);

    ExpressionScope addAttribute(String attribute);

    boolean hasVar(String name);

    void addVar(String variable, TypeName variableClass);

    TypeName getVarType(String name);
}
