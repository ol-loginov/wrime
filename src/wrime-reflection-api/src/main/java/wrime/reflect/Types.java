package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Types {
    private static final List<String> EMPTY_STRINGS = Arrays.asList(new String[0]);
    public final static Type NULL_TYPE = new Type() {
    };

    private static boolean isNullOrNullType(Type type) {
        return type == null || type == NULL_TYPE;
    }

    public static boolean isBoolean(Type type) {
        return !isNullOrNullType(type) && isOneOf(type, boolean.class, Boolean.class);
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
        return getJavaSourceName(type, EMPTY_STRINGS);
    }

    public static String getJavaSourceName(Type type, final List<String> imports) {
        return new TypeVisitor<String>(type) {
            @Override
            protected String visitClass(Class target) {
                String fullName = target.getCanonicalName();
                String className = TypeLocator.getClassName(fullName);

                Map<String, String> importResolvers = new TreeMap<String, String>();
                importResolvers.put(fullName, className);
                importResolvers.put(TypeLocator.getClassNamePrefix(fullName) + ".*", TypeLocator.byteToCodeName(className));

                for (String importPath : imports) {
                    String resolvedName = importResolvers.get(importPath);
                    if (resolvedName != null) {
                        return resolvedName;
                    }
                }
                return fullName;
            }

            @Override
            protected String visitParameterized(ParameterizedType target) {
                StringBuilder builder = new StringBuilder();
                for (Type typeParameter : target.getActualTypeArguments()) {
                    if (builder.length() == 0) {
                        builder.append("<");
                    } else {
                        builder.append(", ");
                    }
                    builder.append(getJavaSourceName(typeParameter, imports));
                }
                builder.append(">");
                return getJavaSourceName(target.getRawType()) + builder.toString();
            }
        }.visit();
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
