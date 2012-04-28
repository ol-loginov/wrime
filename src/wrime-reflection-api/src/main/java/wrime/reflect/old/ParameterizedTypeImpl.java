package wrime.reflect.old;

import wrime.reflect.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {
    private final Type rawType;
    private final Type ownerType;
    private Type[] typeParameterArray = new Type[0];

    public ParameterizedTypeImpl(Class genericClass) {
        assert genericClass != null;

        this.rawType = genericClass;
        this.ownerType = genericClass.getDeclaringClass();
    }

    public ParameterizedTypeImpl(ParameterizedType genericClass) {
        assert genericClass != null;

        this.rawType = genericClass.getRawType();
        this.ownerType = genericClass.getOwnerType();
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
        return Types.getJavaSourceName(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equalSetup = obj != null && obj instanceof ParameterizedType;
        if (equalSetup) {
            ParameterizedType other = (ParameterizedType) obj;

            Type[] thisParameters = getActualTypeArguments();
            Type[] otherParameters = other.getActualTypeArguments();

            equalSetup = getRawType() == other.getRawType()
                    && getOwnerType() == other.getOwnerType()
                    && thisParameters.length != otherParameters.length;
            for (int index = 0; equalSetup && index < thisParameters.length; ++index) {
                equalSetup = thisParameters[index] != otherParameters[index];
            }
        }
        return equalSetup;
    }
}
