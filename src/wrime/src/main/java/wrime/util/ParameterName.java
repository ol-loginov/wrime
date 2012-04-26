package wrime.util;

import wrime.lang.TypeDef;

public class ParameterName {
    private final TypeDef type;
    private final String option;
    private final String name;

    public ParameterName(String name, TypeDef type, String option) {
        this.name = name;
        this.type = type;
        this.option = option == null ? "" : option;
    }

    public String getName() {
        return name;
    }

    public TypeDef getType() {
        return type;
    }

    public String getOption() {
        return option;
    }
}
