package wrime.ast;

public class Name extends Emitter {
    private final String name;

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
