package wrime.ast;

import wrime.reflect.MethodLookup;

import java.util.List;

public class MethodCall extends Emitter {
    private final Emitter invocable;
    private final String methodName;
    private final List<Emitter> arguments;

    private MethodLookup invocation;

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

    public MethodLookup getInvocation() {
        return invocation;
    }

    public void setInvocation(MethodLookup invocation) {
        this.invocation = invocation;
    }

    public String getInvocationName() {
        return invocation == null ? getMethodName() : invocation.getName();
    }
}
