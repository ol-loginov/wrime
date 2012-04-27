package wrime.reflect.old;

import java.lang.reflect.Type;

public class TypeDef extends TypeProxyDelegate {
    public static final TypeDef NULL_TYPE = new TypeDef();

    protected TypeDef() {
        super(null);
    }

    protected TypeDef(TypeProxy proxy) {
        super(proxy);
        if (proxy == null) {
            throw new IllegalArgumentException("Type is null");
        }
    }

    public TypeDef(Type type) {
        this(TypeProxyImpl.create(type, false));
    }

    public boolean isNullType() {
        return this == NULL_TYPE;
    }

    @Override
    public String toString() {
        return isNullType() ? "<null>" : getDelegate().getJavaSourceName();
    }
}
