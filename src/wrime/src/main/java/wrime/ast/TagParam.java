package wrime.ast;

import java.util.ArrayList;
import java.util.List;

public class TagParam extends WrimeTag {
    private final List<LocatableString> options = new ArrayList<LocatableString>();

    private final ClassName className;
    private final LocatableString paramName;

    public TagParam(ClassName className, LocatableString paramName) {
        this.paramName = paramName;
        this.className = className;
    }

    public void setOption(LocatableString value) {
        options.add(value);
    }

    public ClassName getClassName() {
        return className;
    }

    public LocatableString getParamName() {
        return paramName;
    }
}
