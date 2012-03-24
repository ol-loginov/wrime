package wrime.ops;

public class Raw extends Operand {
    private final String text;

    public Raw(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean isStatement() {
        return false;
    }
}
