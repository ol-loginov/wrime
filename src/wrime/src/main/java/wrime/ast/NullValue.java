package wrime.ast;

import wrime.lang.TypeInstance;

public class NullValue extends Emitter {
    public NullValue() {
        setReturnType(TypeInstance.NULL_TYPE);
    }

    @Override
    public String toString() {
        return "null";
    }
}
