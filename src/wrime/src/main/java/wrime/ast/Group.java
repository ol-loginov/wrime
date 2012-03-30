package wrime.ast;

public class Group extends Emitter {
    private final Emitter inner;

    public Group(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }
}
