package wrime.lang;

import java.lang.reflect.Method;

public class MethodDef {
    protected final Method method;
    private TypeDef returnType;

    public MethodDef(Method method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public TypeDef getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeDef returnType) {
        this.returnType = returnType;
    }
}
