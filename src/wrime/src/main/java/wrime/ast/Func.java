package wrime.ast;

import java.util.List;

public class Func extends Emitter {
    private final String functor;
    private final NamePath path;
    private final List<Emitter> arguments;

    public Func(String functor, NamePath path, List<Emitter> arguments) {
        this.functor = functor;
        this.path = path;
        this.arguments = arguments;
    }

    public String getFunctor() {
        return functor;
    }

    public NamePath getPath() {
        return path;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }
}
