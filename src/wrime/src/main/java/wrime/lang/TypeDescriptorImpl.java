package wrime.lang;

import wrime.WrimeException;

import java.lang.reflect.*;

abstract class TypeDescriptorImpl implements TypeDescriptor {
    public abstract Type getType();

    static TypeDescriptorImpl create(Type type) {
        return create(type, true);
    }

    static TypeDescriptorImpl create(Type type, boolean errorIfNull) {
        if (type == null) {
            if (errorIfNull) {
                throw new IllegalArgumentException("type is null");
            }
            return null;
        } else if (isClass(type)) {
            return new DescriptorForClass((Class) type);
        } else if (isParameterizedType(type)) {
            return new DescriptorForParameterizedType((ParameterizedType) type);
        } else if (isWildcard(type)) {
            return new DescriptorForWildcardType((WildcardType) type);
        } else if (isTypeVariable(type)) {
            return new DescriptorForTypeVariable((TypeVariable) type);
        } else {
            throw new IllegalStateException("Type support is not implemented");
        }
    }

    static boolean isParameterizedType(Type type) {
        return ParameterizedType.class.isAssignableFrom(type.getClass());
    }

    static boolean isClass(Type type) {
        return Class.class.isAssignableFrom(type.getClass());
    }

    static boolean isTypeVariable(Type type) {
        return TypeVariable.class.isAssignableFrom(type.getClass());
    }

    private static boolean isWildcard(Type type) {
        return WildcardType.class.isAssignableFrom(type.getClass());
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public TypeDef getSuperclass() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isAssignableFrom(TypeDef other) {
        return isAssignableFrom(other.getDelegate().getType());
    }

    protected boolean isAssignableFrom(Type other) {
        throw new IllegalStateException("Type is not a class");
    }

    @Override
    public TypeDef getComponentType() {
        throw new IllegalStateException("Type is not a class");
    }

    public Method[] getDeclaredMethods() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public String getJavaSourceName() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public TypeDef getTypeParameterOf(Class generic, int index) throws WrimeException {
        throw new IllegalStateException("Type is not generic");
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }
}
