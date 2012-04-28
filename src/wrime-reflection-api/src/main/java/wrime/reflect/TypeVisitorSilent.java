package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVisitorSilent<T> extends TypeVisitor<T> {
    private final T defaultReturnValue;

    public TypeVisitorSilent(Type target, T defaultReturnValue) {
        super(target);
        this.defaultReturnValue = defaultReturnValue;
    }

    @Override
    protected T visitTypeVariable(TypeVariable target) {
        return defaultReturnValue;
    }

    @Override
    protected T visitClass(Class target) {
        return defaultReturnValue;
    }

    @Override
    protected T visitParameterized(ParameterizedType target) {
        return defaultReturnValue;
    }
}
