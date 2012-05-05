package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public abstract class TypeVisitor<T> {
    private final Type target;

    public TypeVisitor(Type target) {
        this.target = target;
    }

    public final T visit() {
        if (Types.isClass(target)) {
            return visitClass((Class) target);
        } else if (Types.isParameterizedType(target)) {
            return visitParameterized((ParameterizedType) target);
        } else if (Types.isTypeVariable(target)) {
            return visitTypeVariable((TypeVariable) target);
        } else if (Types.isWildcard(target)) {
            return visitWildcard((WildcardType) target);
        } else {
            throw new IllegalStateException("visitor is confused");
        }
    }

    protected T visitWildcard(WildcardType target) {
        throw new IllegalStateException("visitor not ready for wildcard type");
    }

    protected T visitTypeVariable(TypeVariable target) {
        throw new IllegalStateException("visitor not ready for typed variable");
    }

    protected T visitClass(Class target) {
        throw new IllegalStateException("visitor not ready for class");
    }

    protected T visitParameterized(ParameterizedType target) {
        throw new IllegalStateException("visitor not ready for parameterized type");
    }
}
