package wrime.ast;

import wrime.lang.TypeName;

public class NullValue extends Emitter {
    public NullValue() {
        setReturnType(TypeName.NULL_TYPE);
    }

    @Override
    public String toString() {
        return "null";
    }
}
