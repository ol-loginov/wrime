package wrime.ast;

public class VariableRef extends Emitter {
    private final String name;

    public VariableRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
