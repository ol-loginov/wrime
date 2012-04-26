package wrime.bytecode;

import wrime.lang.TypeDef;

public interface ExpressionScope {
    boolean hasAttribute(String attribute);

    ExpressionScope addAttribute(String attribute);

    boolean hasVar(String name);

    void addVar(String variable, TypeDef variableClass);

    TypeDef getVarType(String name);
}
