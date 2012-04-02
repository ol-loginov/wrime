package wrime.ast;

public class TagFor extends WrimeTag {
    private Emitter iterable;
    private LocatableString variable;

    public void setIterable(Emitter iterable) {
        this.iterable = iterable;
    }

    public void setVar(LocatableString variable) {
        this.variable = variable;
    }
}
