package wrime.ast;

import wrime.util.TypeName;

public class NullValue extends Emitter {
    public NullValue() {
        setReturnType(TypeName.NULL_TYPE);
    }

    @Override
    public String toString() {
        return "null";
    }
}
