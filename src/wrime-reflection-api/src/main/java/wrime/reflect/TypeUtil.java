package wrime.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtil {
    public static Type getSuperclass(Type type) {
        if (Types.isClass(type)) {
            return ((Class) type).getGenericSuperclass();
        }
        throw new IllegalArgumentException("not implemented for " + type);
    }

    public static Type getComponentType(Type type) {
        throw new IllegalStateException("not implemented");
    }

    public static Type getTypeParameterOf(Type type, Type typeVariableDeclaringClass, int index) {
        throw new IllegalStateException("not implemented");
    }

    public static Method[] getDeclaredMethods(Type type) {
        if (Types.isClass(type)) {
            return ((Class) type).getDeclaredMethods();
        }
        if (Types.isParameterizedType(type)) {
            return getDeclaredMethods(((ParameterizedType) type).getRawType());
        }
        throw new IllegalArgumentException("not implemented for " + type);
    }
}
