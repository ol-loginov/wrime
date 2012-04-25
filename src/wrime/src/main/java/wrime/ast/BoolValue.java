package wrime.ast;

import wrime.lang.TypeInstance;

public class BoolValue extends Emitter {
    private boolean value;

    public BoolValue(boolean value) {
        this.value = value;
        setReturnType(new TypeInstance(boolean.class));
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
