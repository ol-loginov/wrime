package wrime.lang;

import java.lang.reflect.Type;

public class TypeDef extends TypeDescriptorDelegate {
    public static final TypeDef NULL_TYPE = new TypeDef();

    protected TypeDef() {
        super(null);
    }

    protected TypeDef(TypeDescriptor descriptor) {
        super(descriptor);
        if (descriptor == null) {
            throw new IllegalArgumentException("Type is null");
        }
    }

    public TypeDef(Type type) {
        this(TypeDescriptorImpl.create(type, false));
    }

    public boolean isA(Class clazz) {
        return clazz.equals(getDelegate().getType());
    }

    public boolean isNullType() {
        return this == NULL_TYPE;
    }

    @Override
    public String toString() {
        return isNullType() ? "<null>" : getDelegate().getJavaSourceName();
    }
}
