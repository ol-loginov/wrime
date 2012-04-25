package wrime.util;

import wrime.lang.TypeName;

public class ParameterName {
    private final TypeName type;
    private final String option;
    private final String name;

    public ParameterName(String name, TypeName type, String option) {
        this.name = name;
        this.type = type;
        this.option = option == null ? "" : option;
    }

    public String getName() {
        return name;
    }

    public TypeName getType() {
        return type;
    }

    public String getOption() {
        return option;
    }
}
