package wrime.ast;

public class TagIf extends WrimeTag {
    public enum Mode {OPEN, ELIF, ELSE, CLOSE}

    private final Mode mode;
    private Emitter test;

    public TagIf(Mode mode) {
        this(mode, null);
    }

    public TagIf(Mode mode, Emitter test) {
        this.mode = mode;
        this.test = test;
    }

    public Mode getMode() {
        return mode;
    }

    public Emitter getTest() {
        return test;
    }
}
