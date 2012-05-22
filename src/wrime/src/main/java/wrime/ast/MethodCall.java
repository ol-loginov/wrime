package wrime.ast;

import wrime.reflect.MethodLookup;

import java.util.List;

public class MethodCall extends Emitter {
    private final Emitter invocable;
    private final String desiredName;
    private final List<Emitter> arguments;

    private MethodLookup invocation;

    public MethodCall(Emitter invocable, String desiredName, List<Emitter> arguments) {
        this.invocable = invocable;
        this.desiredName = desiredName;
        this.arguments = arguments;
    }

    public boolean hasArguments() {
        return arguments != null && arguments.size() > 0;
    }

    public Emitter getInvocable() {
        return invocable;
    }

    public String getDesiredName() {
        return desiredName;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }

    public void setInvocation(MethodLookup invocation) {
        this.invocation = invocation;
    }

    public String getInvocationName() {
        return invocation.getName();
    }
}
