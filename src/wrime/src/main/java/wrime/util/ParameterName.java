package wrime.util;

import java.lang.reflect.Type;

public class ParameterName {
    private final Type type;
    private final String option;
    private final String name;

    public ParameterName(String name, Type type, String option) {
        this.name = name;
        this.type = type;
        this.option = option == null ? "" : option;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getOption() {
        return option;
    }
}
