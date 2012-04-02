package wrime.ast;

import java.util.ArrayList;
import java.util.List;

public class TagInclude extends WrimeTag {
    private final List<Assignment> arguments = new ArrayList<Assignment>();

    public void addAssignment(Assignment a) {
        arguments.add(a);
    }
}
