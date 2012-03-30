package wrime.ast;

public class Algebraic extends Emitter {
    private final Emitter left;
    private final Emitter right;
    private final AlgebraicRule rule;

    public Algebraic(Emitter left, AlgebraicRule rule, Emitter right) {
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

    public AlgebraicRule getRule() {
        return rule;
    }
}
