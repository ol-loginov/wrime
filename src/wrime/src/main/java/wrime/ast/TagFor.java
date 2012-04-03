package wrime.ast;

public class TagFor extends WrimeTag {
    public enum Mode {
        OPEN,
        CLOSE
    }

    private Emitter iterable;
    private LocatableString variable;
    private Mode mode = Mode.CLOSE;

    public void setIterable(Emitter iterable) {
        this.iterable = iterable;
        if (this.iterable != null) {
            this.mode = Mode.OPEN;
        }
    }

    public void setVar(LocatableString variable) {
        this.variable = variable;
        if (this.variable != null) {
            this.mode = Mode.OPEN;
        }
    }

    public Mode getMode() {
        return mode;
    }

    public Emitter getIterable() {
        return iterable;
    }

    public LocatableString getVariable() {
        return variable;
    }
}
