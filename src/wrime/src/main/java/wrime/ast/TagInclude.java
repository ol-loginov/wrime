package wrime.ast;

import java.util.ArrayList;
import java.util.List;

public class TagInclude extends WrimeTag {
    private final List<Assignment> arguments = new ArrayList<Assignment>();
    private final Emitter source;

    public TagInclude(Emitter source) {
        this.source = source;
    }

    public void addAssignment(Assignment a) {
        arguments.add(a);
    }

    public Emitter getSource() {
        return source;
    }

    public List<Assignment> getArguments() {
        return arguments;
    }
}
