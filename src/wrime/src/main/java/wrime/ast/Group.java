package wrime.ast;

import wrime.lang.TypeDef;

public class Group extends Emitter {
    private final Emitter inner;

    public Group(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }

    @Override
    public TypeDef getReturnType() {
        return super.getReturnType();
    }

    @Override
    public void setReturnType(TypeDef returnType) {
        super.setReturnType(returnType);
    }
}
