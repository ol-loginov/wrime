package wrime.ast;

public class FunctorRef extends Emitter {
    private final String name;

    public FunctorRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
