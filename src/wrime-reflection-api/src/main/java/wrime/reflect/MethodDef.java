package wrime.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodDef {
    protected final Method method;
    private Type returnType;

    public MethodDef(Method method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public Type getReturnType() {
        return returnType;
    }

    public MethodDef setReturnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }
}
