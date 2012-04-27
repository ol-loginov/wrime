package wrime.reflect.old;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {
    private final Type rawType;
    private final Type ownerType;
    private Type[] typeParameterArray;

    public ParameterizedTypeImpl(Class genericClass) {
        this.rawType = genericClass;
        this.ownerType = genericClass.getDeclaringClass();
    }

    public ParameterizedTypeImpl(ParameterizedType genericClass) {
        this.rawType = genericClass.getRawType();
        this.ownerType = genericClass.getRawType();
    }

    public void setTypeParameterArray(Type[] typeParameterArray) {
        for (Type type : typeParameterArray) {
            if (type == null) {
                throw new IllegalArgumentException("Value in typeParameterArray is null");
            }
        }
        this.typeParameterArray = typeParameterArray;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeParameterArray;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public String toString() {
        return rawType.toString();
    }
}
