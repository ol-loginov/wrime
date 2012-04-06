package wrime.util;

import wrime.WrimeException;

import java.lang.reflect.*;

public abstract class TypeWrap {
    public abstract Type getType();

    public static TypeWrap create(Type type) {
        if (isClass(type)) {
            return new ClassWrap((Class) type);
        } else if (isParameterizedType(type)) {
            return new ParameterizedWrap((ParameterizedType) type);
        } else if (isWildcard(type)) {
            return new WildcardWrap((WildcardType) type);
        } else if (isTypeVariable(type)) {
            return new TypeVariableWrap((TypeVariable) type);
        } else {
            throw new IllegalStateException("Type support is not implemented");
        }
    }

    private static boolean isParameterizedType(Type type) {
        return ParameterizedType.class.isAssignableFrom(type.getClass());
    }

    private static boolean isClass(Type type) {
        return Class.class.isAssignableFrom(type.getClass());
    }

    private static boolean isTypeVariable(Type type) {
        return TypeVariable.class.isAssignableFrom(type.getClass());
    }

    private static boolean isWildcard(Type type) {
        return WildcardType.class.isAssignableFrom(type.getClass());
    }

    public boolean isAssignableTo(Type other) {
        return TypeWrap.create(other).isAssignableFrom(getType());
    }

    public boolean isArray() {
        return false;
    }

    public Type getSuperclass() {
        throw new IllegalStateException("not implemented");
    }

    public boolean isAssignableFrom(Type other) {
        throw new IllegalStateException("not implemented");
    }

    public Class getComponentType() {
        throw new IllegalStateException("Type is not a class");
    }

    public Method[] getDeclaredMethods() {
        throw new IllegalStateException("not implemented");
    }

    public String getJavaSourceName() {
        throw new IllegalStateException("not implemented");
    }

    public Type getTypeParameterOf(Class generic, int index) throws WrimeException {
        throw new IllegalStateException("Type is not generic");
    }

    static class ClassWrap extends TypeWrap {
        final Class type;

        public ClassWrap(Class type) {
            this.type = type;
        }

        @Override
        public Class getType() {
            return type;
        }

        @Override
        public boolean isArray() {
            return type.isArray();
        }

        @Override
        public Class getComponentType() {
            return type.getComponentType();
        }

        @Override
        public String getJavaSourceName() {
            return type.getCanonicalName();
        }

        @Override
        public Method[] getDeclaredMethods() {
            return type.getDeclaredMethods();
        }

        @Override
        public boolean isAssignableFrom(Type other) {
            if (Object.class.equals(type)) {
                return true;
            } else if (isClass(other)) {
                return type.isAssignableFrom((Class) other);
            } else if (isParameterizedType(other)) {
                return isAssignableFrom(((ParameterizedType) other).getRawType());
            } else {
                throw new IllegalStateException("Decision is not implemented");
            }
        }

        @Override
        public Type getSuperclass() {
            return type.getGenericSuperclass();
        }

        @Override
        public Type getTypeParameterOf(Class generic, int index) throws WrimeException {
            if (type.equals(generic)) {
                return type.getTypeParameters()[index];
            }
            for (Type genericInterface : type.getGenericInterfaces()) {
                Type result = TypeWrap.create(genericInterface).getTypeParameterOf(generic, index);
                if (result != null) {
                    return result;
                }
            }
            if (null == getSuperclass()) {
                throw new WrimeException("Type " + type + " has no specified type for generic " + generic, null);
            }
            return TypeWrap.create(getSuperclass()).getTypeParameterOf(generic, index);
        }
    }

    static class ParameterizedWrap extends TypeWrap {
        final ParameterizedType type;

        public ParameterizedWrap(ParameterizedType type) {
            this.type = type;
        }

        @Override
        public ParameterizedType getType() {
            return type;
        }

        @Override
        public Type getTypeParameterOf(Class generic, int index) {
            return type.getActualTypeArguments()[index];
        }

        @Override
        public String getJavaSourceName() {
            StringBuilder builder = new StringBuilder();
            builder.append(((Class) type.getRawType()).getName());
            builder.append("<");
            boolean first = true;
            for (Type typeArgument : type.getActualTypeArguments()) {
                if (!first) {
                    builder.append(", ");
                }
                first = false;
                builder.append(TypeWrap.create(typeArgument).getJavaSourceName());
            }
            builder.append(">");
            return builder.toString();
        }
    }

    static class WildcardWrap extends TypeWrap {
        final WildcardType type;

        WildcardWrap(WildcardType type) {
            this.type = type;
        }

        @Override
        public WildcardType getType() {
            return type;
        }

        @Override
        public String getJavaSourceName() {
            Type[] uppers = type.getUpperBounds();
            if (uppers == null || uppers.length != 1) {
                throw new IllegalStateException("many upper bounds is not implemented");
            }
            return TypeWrap.create(uppers[0]).getJavaSourceName();
        }
    }

    static class TypeVariableWrap extends TypeWrap {
        final TypeVariable type;

        public TypeVariableWrap(TypeVariable type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getJavaSourceName() {
            return new ClassWrap(Object.class).getJavaSourceName();
        }
    }
}
