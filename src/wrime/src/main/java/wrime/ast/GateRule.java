package wrime.ast;

public enum GateRule {
    AND("&&"),
    XOR("^"),
    OR("||");

    private final String javaSymbol;

    private GateRule(String javaSymbol) {
        this.javaSymbol = javaSymbol;
    }

    public String getJavaSymbol() {
        return javaSymbol;
    }
}
