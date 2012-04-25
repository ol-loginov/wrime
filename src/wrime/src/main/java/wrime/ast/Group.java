package wrime.ast;

import wrime.lang.TypeInstance;

public class Group extends Emitter {
    private final Emitter inner;

    public Group(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }

    @Override
    public TypeInstance getReturnType() {
        return super.getReturnType();
    }

    @Override
    public void setReturnType(TypeInstance returnType) {
        super.setReturnType(returnType);
    }
}
