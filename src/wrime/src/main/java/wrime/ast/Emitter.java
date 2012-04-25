package wrime.ast;

import wrime.lang.TypeInstance;

public abstract class Emitter extends Locatable {
    private TypeInstance returnType;

    public TypeInstance getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeInstance returnType) {
        this.returnType = returnType;
    }
}
