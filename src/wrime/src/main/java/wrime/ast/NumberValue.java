package wrime.ast;

public class NumberValue extends Emitter {
    private String text;

    public NumberValue(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
