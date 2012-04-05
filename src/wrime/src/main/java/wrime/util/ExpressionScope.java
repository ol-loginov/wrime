package wrime.util;

public interface ExpressionScope {
    boolean hasVar(String name);

    TypeName getVarType(String name);

    boolean hasFunctor(String name);

    TypeName getFunctorType(String name);
}
