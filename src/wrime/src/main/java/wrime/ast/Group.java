package wrime.ast;

import wrime.util.TypeName;

public class Group extends Emitter {
    private final Emitter inner;

    public Group(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }

    @Override
    public TypeName getReturnType() {
        return super.getReturnType();
    }

    @Override
    public void setReturnType(TypeName returnType) {
        super.setReturnType(returnType);
    }
}
