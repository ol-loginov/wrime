package wrime.reflect.old;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVariableProxy extends TypeProxyImpl {
    final TypeVariable type;

    public TypeVariableProxy(TypeVariable type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getJavaSourceName() {
        return new ClassProxy(Object.class).getJavaSourceName();
    }
}