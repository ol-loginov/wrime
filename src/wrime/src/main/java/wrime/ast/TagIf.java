package wrime.ast;

public class TagIf extends WrimeTag {
    public enum Mode {OPEN, ELIF, ELSE, CLOSE}

    private final Mode mode;
    private Emitter test;

    public TagIf(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public Emitter getTest() {
        return test;
    }

    public void setTest(Emitter test) {
        this.test = test;
    }
}
