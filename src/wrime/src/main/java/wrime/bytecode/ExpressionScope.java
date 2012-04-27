package wrime.bytecode;

import java.lang.reflect.Type;

public interface ExpressionScope {
    boolean hasAttribute(String attribute);

    ExpressionScope addAttribute(String attribute);

    boolean hasVar(String name);

    void addVar(String variable, Type variableClass);

    Type getVarType(String name);
}
