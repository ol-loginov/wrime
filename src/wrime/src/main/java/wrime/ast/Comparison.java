package wrime.ast;

public class Comparison extends Emitter {
    private final Emitter left;
    private final Emitter right;
    private final ComparisonRule rule;

    public Comparison(Emitter left, ComparisonRule rule, Emitter right) {
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

    public ComparisonRule getRule() {
        return rule;
    }
}
