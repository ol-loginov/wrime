package wrime.ast;

public class Assignment {
    private Emitter emitter;
    private LocatableString var;

    public Emitter getEmitter() {
        return emitter;
    }

    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }

    public LocatableString getVar() {
        return var;
    }

    public void setVar(LocatableString var) {
        this.var = var;
    }
}
