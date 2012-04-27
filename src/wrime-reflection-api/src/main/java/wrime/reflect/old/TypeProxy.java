package wrime.reflect.old;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public interface TypeProxy {
    Type getType();

    boolean isParameterized();

    boolean isArray();

    boolean isVoid();

    boolean isBoolean();

    String getJavaSourceName();

    boolean isAssignableFrom(TypeDef other);

    TypeDef getSuperclass();

    TypeDef getComponentType();

    TypeDef getTypeParameterOf(Class generic, int index);

    Method[] getDeclaredMethods();

    Type getTypeParameter(TypeVariable typeVariable);
}
