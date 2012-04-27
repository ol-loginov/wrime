package wrime.reflect.old;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ParameterizedTypeResolver {
    public static ParameterizedType resolveType(ParameterizedType type, TypeVariableMap localTypes, Type declaringClass) {
        ParameterizedTypeImpl result = new ParameterizedTypeImpl(type);
        Type[] typeArguments = type.getActualTypeArguments();
        Type[] typeResolved = new Type[typeArguments.length];
        for (int i = 0; i < typeArguments.length; ++i) {
            if (typeArguments[i] instanceof TypeVariable) {
                typeResolved[i] = findTypeVariable((TypeVariable) typeArguments[i], localTypes, declaringClass);
            } else {
                typeResolved[i] = typeArguments[i];
            }
        }
        result.setTypeParameterArray(typeResolved);
        return result;
    }

    public static Type findTypeVariable(TypeVariable variable, TypeVariableMap localTypes, Type declaringClass) {
        for (int i = 0; i < localTypes.size(); ++i) {
            TypeVariable localTypeVariable = localTypes.getVariable(i);
            if (localTypeVariable.getName().equals(variable.getName())) {
                return localTypes.getVariableType(i);
            }
        }
        if (declaringClass instanceof ParameterizedType) {
            throw new IllegalStateException("not implemented");
        }
        throw new IllegalArgumentException("Type variable " + variable + " is unresolvable");
    }
}
