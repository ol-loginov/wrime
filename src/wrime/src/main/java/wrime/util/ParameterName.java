package wrime.util;

import wrime.lang.TypeInstance;

public class ParameterName {
    private final TypeInstance type;
    private final String option;
    private final String name;

    public ParameterName(String name, TypeInstance type, String option) {
        this.name = name;
        this.type = type;
        this.option = option == null ? "" : option;
    }

    public String getName() {
        return name;
    }

    public TypeInstance getType() {
        return type;
    }

    public String getOption() {
        return option;
    }
}
