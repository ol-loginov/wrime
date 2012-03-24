package wrime;

import java.lang.reflect.Type;

public class TypeName {
    private Type type;
    private String alias;

    public TypeName() {
    }

    public TypeName(Type type) {
        this.type = type;
    }

    public boolean isVoid() {
        return Void.TYPE.equals(type) || void.class.equals(type);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
