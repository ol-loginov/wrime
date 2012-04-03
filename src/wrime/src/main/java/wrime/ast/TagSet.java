package wrime.ast;

public class TagSet extends WrimeTag {
    private final LocatableString variable;
    private final Emitter value;

    public TagSet(LocatableString variable, Emitter value) {
        super("set");
        this.value = value;
        this.variable = variable;
    }

    public LocatableString getVariable() {
        return variable;
    }

    public Emitter getValue() {
        return value;
    }
}
