package wrime.ast;

import java.lang.reflect.Method;
import java.util.List;

public class MethodCall extends Emitter {
    private final Emitter invocable;
    private final String methodName;
    private final List<Emitter> arguments;

    private Method invocation;

    public MethodCall(Emitter invocable, String methodName, List<Emitter> arguments) {
        this.invocable = invocable;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public boolean hasArguments() {
        return arguments != null && arguments.size() > 0;
    }

    public Emitter getInvocable() {
        return invocable;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }

    public Method getInvocation() {
        return invocation;
    }

    public void setInvocation(Method invocation) {
        this.invocation = invocation;
    }

    public String getInvocationName() {
        return invocation == null ? getMethodName() : invocation.getName();
    }
}
