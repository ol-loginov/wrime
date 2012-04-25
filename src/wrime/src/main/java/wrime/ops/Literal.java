package wrime.ops;

import wrime.lang.TypeName;

public class Literal extends Operand {
    public static enum Value {
        STRING,
        INTEGER
    }

    private final Value value;
    private final String string;
    private final int integer;

    public Literal(String string) {
        this(Value.STRING, string, 0, String.class);
    }

    public Literal(int integer) {
        this(Value.INTEGER, null, integer, int.class);
    }

    private Literal(Value value, String string, int integer, Class result) {
        this.value = value;
        this.integer = integer;
        this.string = string;
        setResult(new TypeName(result));
    }

    public String getString() {
        return string;
    }

    public int getInteger() {
        return integer;
    }

    public Value getValue() {
        return value;
    }
}
