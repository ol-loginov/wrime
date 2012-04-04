package wrime.ast;

import java.util.List;

public class Funcall extends Emitter {
    public enum Mode {
        FUNCTOR_METHOD,
        VARIABLE,
        OBJECT_METHOD
    }

    private final String functor;
    private final Emitter invocable;
    private final LocatableString method;
    private final List<Emitter> arguments;

    private Mode mode;

    private Funcall(String functor, Emitter invocable, LocatableString method, List<Emitter> arguments) {
        this.functor = functor;
        this.invocable = invocable;
        this.method = method;
        this.arguments = arguments;
    }

    public Funcall(String functor, LocatableString method, List<Emitter> arguments) {
        this(functor, null, method, arguments);
        mode = functor == null ? Mode.VARIABLE : Mode.FUNCTOR_METHOD;
    }

    public Funcall(Emitter invocable, LocatableString method, List<Emitter> arguments) {
        this(null, invocable, method, arguments);
        mode = Mode.OBJECT_METHOD;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getFunctor() {
        return functor;
    }

    public Emitter getInvocable() {
        return invocable;
    }

    public LocatableString getMethodOrVariableName() {
        return method;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }

    public boolean hasArguments() {
        return arguments != null && arguments.size() > 0;
    }
}
