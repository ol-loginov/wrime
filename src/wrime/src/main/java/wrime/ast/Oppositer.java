package wrime.ast;

public class Oppositer extends Emitter {
    private final Emitter inner;

    public Oppositer(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }
}
