package wrime.util;

import java.lang.reflect.Type;

public class TypeName {
    public static final TypeName NULL_TYPE = new TypeName(null);

    private final Type type;
    private final String alias;

    public TypeName(Type type) {
        this(type, null);
    }

    public TypeName(Type type, String alias) {
        this.type = type;
        this.alias = alias;
    }

    public boolean isVoid() {
        return Void.TYPE.equals(type) || void.class.equals(type);
    }

    public Type getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isNullType() {
        return this == NULL_TYPE;
    }

    @Override
    public String toString() {
        return isNullType() ? "<null>" : type.toString();
    }
}
