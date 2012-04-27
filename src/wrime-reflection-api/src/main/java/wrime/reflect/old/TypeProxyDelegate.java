package wrime.reflect.old;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

class TypeProxyDelegate implements TypeProxy {
    private final TypeProxy delegate;

    public TypeProxyDelegate(TypeProxy delegate) {
        this.delegate = delegate;
    }

    public TypeProxy getDelegate() {
        return delegate;
    }

    @Override
    public Type getType() {
        return delegate.getType();
    }

    @Override
    public String getJavaSourceName() {
        return delegate.getJavaSourceName();
    }

    @Override
    public boolean isAssignableFrom(TypeDef other) {
        return delegate.isAssignableFrom(other);
    }

    @Override
    public TypeDef getComponentType() {
        return delegate.getComponentType();
    }

    @Override
    public boolean isArray() {
        return delegate.isArray();
    }

    @Override
    public TypeDef getSuperclass() {
        return delegate.getSuperclass();
    }

    @Override
    public Type getTypeParameter(TypeVariable typeVariable) {
        return delegate.getTypeParameter(typeVariable);
    }

    @Override
    public TypeDef getTypeParameterOf(Class generic, int index) {
        return delegate.getTypeParameterOf(generic, index);
    }

    @Override
    public boolean isBoolean() {
        return delegate.isBoolean();
    }

    @Override
    public Method[] getDeclaredMethods() {
        return delegate.getDeclaredMethods();
    }

    @Override
    public boolean isVoid() {
        return delegate.isVoid();
    }

    @Override
    public boolean isParameterized() {
        return delegate.isParameterized();
    }
}
