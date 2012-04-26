package wrime.lang;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DescriptorForParameterizedType extends TypeDescriptorImpl {
    final ParameterizedType type;

    public DescriptorForParameterizedType(ParameterizedType type) {
        this.type = type;
    }

    @Override
    public ParameterizedType getType() {
        return type;
    }

    @Override
    public TypeDef getTypeParameterOf(Class generic, int index) {
        return new TypeDef(type.getActualTypeArguments()[index]);
    }

    @Override
    public TypeDef getSuperclass() {
        return TypeDescriptorImpl.create(type.getRawType()).getSuperclass();
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
            builder.append(TypeDescriptorImpl.create(typeArgument).getJavaSourceName());
        }
        builder.append(">");
        return builder.toString();
    }

    @Override
    public Method[] getDeclaredMethods() {
        return TypeDescriptorImpl.create(type.getRawType()).getDeclaredMethods();
    }
}