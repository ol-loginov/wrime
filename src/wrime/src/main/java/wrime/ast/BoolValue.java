package wrime.ast;

import wrime.lang.TypeDef;

public class BoolValue extends Emitter {
    private boolean value;

    public BoolValue(boolean value) {
        this.value = value;
        setReturnType(new TypeDef(boolean.class));
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
