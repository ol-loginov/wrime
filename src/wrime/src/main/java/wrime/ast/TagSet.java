package wrime.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagSet extends WrimeTag {
    public static class Variable {
        public String variable;
        public Emitter value;
    }

    private final List<Variable> variables = new ArrayList<Variable>();

    public TagSet(LocatableString variable, Emitter value) {
        super("set");
        addVariable(variable, value);
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public void addVariable(final LocatableString varName, final Emitter varValue) {
        variables.add(new Variable() {{
            this.variable = varName.getText();
            this.value = varValue;
        }});
    }
}
