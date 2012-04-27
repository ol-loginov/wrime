package wrime.ast;

public class BoolValue extends Emitter {
    private boolean value;

    public BoolValue(boolean value) {
        this.value = value;
        setReturnType(boolean.class);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
