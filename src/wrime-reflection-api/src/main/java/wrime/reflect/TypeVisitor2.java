package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVisitor2<T> {
    private final Type target;

    public TypeVisitor2(Type target) {
        this.target = target;
    }

    public final T visit(T arg) {
        if (Types.isClass(target)) {
            visitClass((Class) target, arg);
        } else if (Types.isParameterizedType(target)) {
            visitParameterized((ParameterizedType) target, arg);
        } else if (Types.isTypeVariable(target)) {
            visitTypeVariable((TypeVariable) target, arg);
        } else {
            throw new IllegalStateException("visitor is confused");
        }
        return arg;
    }

    protected void visitTypeVariable(TypeVariable target, T arg) {
        throw new IllegalStateException("visitor not ready for typed variable");
    }

    protected void visitClass(Class target, T arg) {
        throw new IllegalStateException("visitor not ready for class");
    }

    protected void visitParameterized(ParameterizedType target, T arg) {
        throw new IllegalStateException("visitor not ready for parameterized type");
    }
}
