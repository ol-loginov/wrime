package wrime.ast;

import java.util.List;

public class TagCustom extends WrimeTag {
    private final LocatableString name;
    private final List<Emitter> arguments;

    public TagCustom(LocatableString name, List<Emitter> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public LocatableString getName() {
        return name;
    }

    public List<Emitter> getArguments() {
        return arguments;
    }
}
