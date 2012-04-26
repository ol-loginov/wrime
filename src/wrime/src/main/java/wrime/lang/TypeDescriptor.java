package wrime.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface TypeDescriptor {
    Type getType();

    boolean isArray();

    boolean isVoid();

    boolean isBoolean();

    String getJavaSourceName();

    boolean isAssignableFrom(TypeDef other);

    TypeDef getSuperclass();

    TypeDef getComponentType();

    TypeDef getTypeParameterOf(Class generic, int index);

    Method[] getDeclaredMethods();
}
