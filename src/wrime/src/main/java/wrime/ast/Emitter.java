package wrime.ast;

import wrime.lang.TypeDef;

public abstract class Emitter extends Locatable {
    private TypeDef returnType;

    public TypeDef getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeDef returnType) {
        this.returnType = returnType;
    }
}
