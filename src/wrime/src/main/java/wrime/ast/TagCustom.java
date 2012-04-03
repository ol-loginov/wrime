package wrime.ast;

import java.util.List;

public class TagCustom extends WrimeTag {
    private final List<Emitter> arguments;

    public TagCustom(String name, List<Emitter> arguments) {
        super(name, true);
        this.arguments = arguments;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }
}
