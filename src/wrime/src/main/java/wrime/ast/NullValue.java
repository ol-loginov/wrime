package wrime.ast;

import wrime.reflect.Types;

public class NullValue extends Emitter {
    public NullValue() {
        setReturnType(Types.NULL_TYPE);
    }

    @Override
    public String toString() {
        return "null";
    }
}
