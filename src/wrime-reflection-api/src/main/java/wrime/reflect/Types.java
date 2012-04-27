package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class Types {
    public final static Type NULL_TYPE = new Type() {
    };

    private static boolean isNullOrNullType(Type type) {
        return type == null || type == NULL_TYPE;
    }

    public static boolean isBoolean(Type type) {
        return !isNullOrNullType(type) && isOneOf(type, boolean.class, Boolean.TYPE);
    }

    public static int getNumberTypeWeight(Type a) {
        if (isOneOf(a, byte.class, Byte.class))
            return 0;
        if (isOneOf(a, short.class, Short.class))
            return 1;
        if (isOneOf(a, int.class, Integer.class))
            return 2;
        if (isOneOf(a, long.class, Long.class))
            return 3;
        if (isOneOf(a, float.class, Float.class))
            return 4;
        if (isOneOf(a, double.class, Double.class))
            return 5;
        throw new IllegalArgumentException("cannot work with number type " + a);
    }

    public static boolean isAnyNumber(Type type) {
        return !isNullOrNullType(type) && isOneOf(type,
                byte.class, Byte.class,
                short.class, Short.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class);
    }

    public static boolean isOneOf(Type type, Class... types) {
        for (Class clazz : types) {
            if (clazz.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static String getJavaSourceName(Type type) {
        if (isClass(type)) {
            return ((Class) type).getCanonicalName();
        }
        throw new IllegalStateException("not implemented");
    }

    public static boolean isWritable(Type returnType) {
        return returnType != null && !isOneOf(returnType, Void.class);
    }

    public static boolean isParameterizedType(Type type) {
        return ParameterizedType.class.isAssignableFrom(type.getClass());
    }

    public static boolean isClass(Type type) {
        return Class.class.isAssignableFrom(type.getClass());
    }

    public static boolean isTypeVariable(Type type) {
        return TypeVariable.class.isAssignableFrom(type.getClass());
    }

    public static boolean isWildcard(Type type) {
        return WildcardType.class.isAssignableFrom(type.getClass());
    }
}
