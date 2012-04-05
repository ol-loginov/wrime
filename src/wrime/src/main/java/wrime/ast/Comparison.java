package wrime.ast;

public class Comparison extends Emitter {
    public static enum Rule {
        Less("<"),
        Greater(">"),
        LessOrEqual("<="),
        GreaterOrEqual(">="),
        Equal("=="),
        NotEqual("!=");

        private final String javaSymbol;

        private Rule(String javaSymbol) {
            this.javaSymbol = javaSymbol;
        }

        public String getJavaSymbol() {
            return javaSymbol;
        }
    }

    private final Emitter left;
    private final Emitter right;
    private final Rule rule;

    public Comparison(Emitter left, Rule rule, Emitter right) {
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

    public Rule getRule() {
        return rule;
    }
}
