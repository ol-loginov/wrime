package wrime.ast;

import java.lang.reflect.Type;

public class Group extends Emitter {
    private final Emitter inner;

    public Group(Emitter inner) {
        this.inner = inner;
    }

    public Emitter getInner() {
        return inner;
    }

    @Override
    public Type getReturnType() {
        return super.getReturnType();
    }

    @Override
    public void setReturnType(Type returnType) {
        super.setReturnType(returnType);
    }
}
