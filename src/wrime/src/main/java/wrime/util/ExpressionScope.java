package wrime.util;

public interface ExpressionScope {
    TypeName getVarType(String name);

    boolean hasVar(String name);
}
