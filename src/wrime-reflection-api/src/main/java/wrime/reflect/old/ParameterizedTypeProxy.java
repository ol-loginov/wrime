package wrime.reflect.old;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ParameterizedTypeProxy extends TypeProxyImpl {
    final ParameterizedType type;

    public ParameterizedTypeProxy(ParameterizedType type) {
        this.type = type;
    }

    @Override
    public ParameterizedType getType() {
        return type;
    }

    @Override
    public boolean isParameterized() {
        return true;
    }

    @Override
    public Type getTypeParameter(TypeVariable typeVariable) {
        assert type.getRawType() instanceof Class;
        TypeVariable[] typeVariables = ((Class) type.getRawType()).getTypeParameters();
        for (int i = 0; i < typeVariables.length; ++i) {
            if (typeVariable.getName().equals(typeVariables[i].getName())) {
                return type.getActualTypeArguments()[i];
            }
        }
        throw new IllegalStateException("Type " + type + " has no type variable " + typeVariable);
    }

    @Override
    public TypeDef getTypeParameterOf(Class generic, int index) {
        return new TypeDef(type.getActualTypeArguments()[index]);
    }

    @Override
    public TypeDef getSuperclass() {
        return TypeProxyImpl.create(type.getRawType()).getSuperclass();
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
            builder.append(TypeProxyImpl.create(typeArgument).getJavaSourceName());
        }
        builder.append(">");
        return builder.toString();
    }

    @Override
    public Method[] getDeclaredMethods() {
        return TypeProxyImpl.create(type.getRawType()).getDeclaredMethods();
    }
}