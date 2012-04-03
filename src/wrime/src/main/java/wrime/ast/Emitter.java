package wrime.ast;

import wrime.util.TypeName;

public abstract class Emitter extends Locatable {
    private TypeName returnType;

    public TypeName getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeName returnType) {
        this.returnType = returnType;
    }

    public boolean isReturnTypeResolvable() {
        return true;
    }
}
