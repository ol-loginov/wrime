package wrime.ast;

public enum ComparisonRule {
    Less("<"),
    Greater(">"),
    LessOrEqual("<="),
    GreaterOrEqual(">="),
    Equal("=="),
    NotEqual("!=");

    private final String javaSymbol;

    private ComparisonRule(String javaSymbol) {
        this.javaSymbol = javaSymbol;
    }

    public String getJavaSymbol() {
        return javaSymbol;
    }
}
