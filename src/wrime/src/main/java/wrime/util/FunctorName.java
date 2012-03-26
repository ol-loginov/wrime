package wrime.util;

public class FunctorName {
    private final String name;
    private final Class type;
    private final String field;

    public FunctorName(String name, Class type, String field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public String getField() {
        return field;
    }
}
