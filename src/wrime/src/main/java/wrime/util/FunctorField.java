package wrime.util;

public class FunctorField {
    private final String name;
    private final Class type;
    private final String field;

    public FunctorField(String name, Class type, String field) {
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
