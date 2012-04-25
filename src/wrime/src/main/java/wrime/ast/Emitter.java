package wrime.ast;

import wrime.lang.TypeName;

public abstract class Emitter extends Locatable {
    private TypeName returnType;

    public TypeName getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeName returnType) {
        this.returnType = returnType;
    }
}
