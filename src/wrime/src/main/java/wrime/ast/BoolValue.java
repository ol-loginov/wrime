package wrime.ast;

public class BoolValue extends Emitter {
    private boolean value;

    public BoolValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
