package wrime.reflect.old;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassProxy extends TypeProxyImpl {
    final Class type;

    public ClassProxy(Class type) {
        this.type = type;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public boolean isArray() {
        return type.isArray();
    }

    @Override
    public TypeDef getComponentType() {
        return new TypeDef(type.getComponentType());
    }

    @Override
    public String getJavaSourceName() {
        return type.getCanonicalName();
    }

    @Override
    public Method[] getDeclaredMethods() {
        return type.getDeclaredMethods();
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (Object.class.equals(type)) {
            return true;
        } else if (TypeProxyImpl.isClass(other)) {
            return type.isAssignableFrom((Class) other);
        } else if (TypeProxyImpl.isParameterizedType(other)) {
            return isAssignableFrom(((ParameterizedType) other).getRawType());
        } else {
            throw new IllegalStateException("Decision is not implemented");
        }
    }

    @Override
    public TypeDef getSuperclass() {
        Type superclass = type.getGenericSuperclass();
        return superclass == null ? null : new TypeDef(superclass);
    }

    @Override
    public TypeDef getTypeParameterOf(Class generic, int index) {
        if (type.equals(generic)) {
            return new TypeDef(type.getTypeParameters()[index]);
        }
        for (Type genericInterface : type.getGenericInterfaces()) {
            TypeDef result = TypeProxyImpl.create(genericInterface).getTypeParameterOf(generic, index);
            if (result != null) {
                return result;
            }
        }
        if (null == getSuperclass()) {
            throw new NoClassDefFoundError("Type " + type + " has no specified type for generic " + generic);
        }
        return getSuperclass().getTypeParameterOf(generic, index);
    }

    @Override
    public boolean isVoid() {
        return Void.TYPE.equals(type) || void.class.equals(type);
    }

    @Override
    public boolean isBoolean() {
        return Boolean.TYPE.equals(type) || boolean.class.equals(type);
    }
}