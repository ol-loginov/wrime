package wrime.ast;

public enum AlgebraicRule {
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
    MOD("%");

    private final String javaSymbol;

    private AlgebraicRule(String javaSymbol) {
        this.javaSymbol = javaSymbol;
    }

    public String getJavaSymbol() {
        return javaSymbol;
    }
}
