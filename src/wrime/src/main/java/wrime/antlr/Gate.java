package wrime.antlr;

public class Gate extends Emitter {
    private final Emitter left;
    private final Emitter right;
    private final GateRule rule;

    public Gate(Emitter left, GateRule rule, Emitter right) {
        this.rule = rule;
        this.left = left;
        this.right = right;
    }

    public Emitter getLeft() {
        return left;
    }

    public Emitter getRight() {
        return right;
    }

    public GateRule getRule() {
        return rule;
    }
}
