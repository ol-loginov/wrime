package wrime.lang;

import java.lang.reflect.Type;

public class TypeInstance {
    public static final TypeInstance NULL_TYPE = new TypeInstance();

    private final TypeDescriptor descriptor;
    private final boolean voidFlag;
    private final boolean booleanFlag;

    private TypeInstance() {
        this.descriptor = null;
        this.voidFlag = false;
        this.booleanFlag = false;
    }

    public TypeInstance(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }
        this.descriptor = TypeDescriptor.create(type);
        this.voidFlag = Void.TYPE.equals(type) || void.class.equals(type);
        this.booleanFlag = Boolean.TYPE.equals(type) || boolean.class.equals(type);
    }

    public boolean isVoid() {
        return voidFlag;
    }

    public boolean isBoolean() {
        return booleanFlag;
    }

    public boolean isA(Class clazz) {
        return clazz.equals(descriptor.getType());
    }

    public TypeDescriptor getDescriptor() {
        return descriptor;
    }

    public boolean isNullType() {
        return this == NULL_TYPE;
    }

    @Override
    public String toString() {
        return isNullType() ? "<null>" : descriptor.getJavaSourceName();
    }
}
