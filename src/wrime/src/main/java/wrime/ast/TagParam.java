package wrime.ast;

import wrime.reflect.ClassName;

import java.util.ArrayList;
import java.util.List;

public class TagParam extends WrimeTag {
    private final List<LocatableString> options = new ArrayList<LocatableString>();

    private final ClassName className;
    private final LocatableString paramName;

    public TagParam(ClassName className, LocatableString paramName) {
        super("param");
        this.paramName = paramName;
        this.className = className;
    }

    public void setOption(LocatableString value) {
        options.add(value);
    }

    public List<LocatableString> getOptions() {
        return options;
    }

    public ClassName getClassName() {
        return className;
    }

    public LocatableString getParamName() {
        return paramName;
    }
}
