package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeConverter {
    public static boolean hasParameterizedBounds(Type[] bounds) {
        return bounds.length > 1 || (bounds.length == 1 && !Object.class.equals(bounds[0]));
    }

    public static boolean passBounds(Type passedType, Type[] bounds) {
        for (Type type : bounds) {
            if (type.equals(Object.class)) {
                continue;
            }
            if (Types.isClass(type)) {
                if (!isAssignable(type, passedType)) {
                    return false;
                }
            } else {
                throw new IllegalStateException("not implemented");
            }
        }
        return true;
    }

    private static interface Tester {
        boolean isAssignable(Type source);
    }

    private static final Map<Class, Tester> customConverters;

    static {
        customConverters = new HashMap<Class, Tester>() {{
            put(Object.class, new TesterResult(true));

            put(byte.class, new TesterOnlyAssignable(byte.class, Byte.class));
            put(Byte.class, get(byte.class));
            put(short.class, new TesterOnlyAssignableOrDelegate(get(byte.class), short.class, Short.class));
            put(Short.class, get(short.class));
            put(int.class, new TesterOnlyAssignableOrDelegate(get(short.class), int.class, Integer.class));
            put(Integer.class, get(int.class));
            put(long.class, new TesterOnlyAssignableOrDelegate(get(int.class), long.class, Long.class));
            put(Long.class, get(long.class));
        }};
    }

    public static boolean isAssignable(Type destination, Type source) {
        if (Types.isClass(destination)) {
            return isAssignable((Class) destination, source);
        }
        throw new IllegalStateException("not implemented for " + destination);
    }

    public static boolean isAssignable(Class destination, Type source) {
        Tester tester = customConverters.get(destination);
        if (tester != null) {
            return tester.isAssignable(source);
        }
        if (Types.isClass(source)) {
            return destination.isAssignableFrom((Class) source);
        } else if (Types.isParameterizedType(source)) {
            return isAssignable(destination, ((ParameterizedType) source).getRawType());
        }
        throw new IllegalStateException("not implemented for Class from " + source);
    }

    private static class TesterResult implements Tester {
        private final boolean result;

        private TesterResult(boolean result) {
            this.result = result;
        }

        @Override
        public boolean isAssignable(Type source) {
            return result;
        }
    }

    private static class TesterOnlyAssignable implements Tester {
        private final Class[] assignableTypes;

        public TesterOnlyAssignable(Class... assignableTypes) {
            this.assignableTypes = assignableTypes;
        }

        @Override
        public boolean isAssignable(Type source) {
            return Types.isOneOf(source, assignableTypes);
        }
    }

    private static class TesterOnlyAssignableOrDelegate extends TesterOnlyAssignable {
        private final Tester delegate;

        private TesterOnlyAssignableOrDelegate(Tester delegate, Class... assignableTypes) {
            super(assignableTypes);
            this.delegate = delegate;
        }

        @Override
        public boolean isAssignable(Type source) {
            return super.isAssignable(source) || delegate.isAssignable(source);
        }
    }
}
