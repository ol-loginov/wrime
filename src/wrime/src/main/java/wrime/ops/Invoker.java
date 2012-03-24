package wrime.ops;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Invoker extends Operand {
    private Operand invocable;
    private Method method;
    private String methodName;
    private final List<Operand> parameters = new ArrayList<Operand>();

    public List<Operand> getParameters() {
        return parameters;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Operand getInvocable() {
        return invocable;
    }

    public void setInvocable(Operand invocable) {
        this.invocable = invocable;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
