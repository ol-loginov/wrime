package wrime.ast;

public class Inverter extends Emitter {
    private final Emitter inner;

    public Inverter(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }

    public boolean isReturnTypeResolvable() {
        return inner.isReturnTypeResolvable();
    }
}
