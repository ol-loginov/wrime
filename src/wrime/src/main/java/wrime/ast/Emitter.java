package wrime.ast;

import java.lang.reflect.Type;

public abstract class Emitter extends Locatable {
    private Type returnType;

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
}
