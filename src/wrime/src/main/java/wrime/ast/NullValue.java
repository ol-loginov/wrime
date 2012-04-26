package wrime.ast;

import wrime.lang.TypeDef;

public class NullValue extends Emitter {
    public NullValue() {
        setReturnType(TypeDef.NULL_TYPE);
    }

    @Override
    public String toString() {
        return "null";
    }
}
