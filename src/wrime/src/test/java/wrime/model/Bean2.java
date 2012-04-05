package wrime.model;

public class Bean2 implements Face1 {
    private int integer;
    private String string;

    public int getInteger() {
        return integer;
    }

    public String getString() {
        return string;
    }

    public void call(int value) {
    }

    public Bean2 call(String value) {
        return this;
    }

    public Bean2 callSelf(int value) {
        return this;
    }

    public void varg(int calls, int... object) {
    }

    public void varg(int calls, Object... object) {
    }

    public void void_method(String value) {
    }
}
