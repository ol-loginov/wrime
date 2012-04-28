package wrime.reflect.old;

import wrime.reflect.TypeVisitor;
import wrime.reflect.TypeVisitor2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ParameterizedTypeResolver {
    public static ParameterizedType resolveType(ParameterizedType type, final TypeVariableMap localTypes, final Type declaringClass) {
        final TypeVariableMap declaringTypeMap = createTypeMap(declaringClass);
        declaringTypeMap.putAll(localTypes);

        ParameterizedTypeImpl result = new ParameterizedTypeImpl(type);
        Type[] typeArguments = type.getActualTypeArguments();
        Type[] typeResolved = new Type[typeArguments.length];
        for (int i = 0; i < typeArguments.length; ++i) {
            typeResolved[i] = new TypeVisitor<Type>(typeArguments[i]) {
                @Override
                protected Type visitClass(Class target) {
                    return target;
                }

                @Override
                protected Type visitTypeVariable(TypeVariable target) {
                    return findTypeVariable(target, declaringTypeMap);
                }

                @Override
                protected Type visitParameterized(ParameterizedType target) {
                    return resolveType(target, declaringTypeMap, declaringClass);
                }
            }.visit();
        }
        result.setTypeParameterArray(typeResolved);
        return result;
    }

    public static Type findTypeVariable(TypeVariable variable, TypeVariableMap localTypes, Type declaringClass) {
        TypeVariableMap declaringClassMap = createTypeMap(declaringClass);
        declaringClassMap.putAll(localTypes);
        return findTypeVariable(variable, declaringClassMap);
    }

    private static TypeVariableMap createTypeMap(Type declaringClass) {
        return new TypeVisitor2<TypeVariableMap>(declaringClass) {
            @Override
            protected void visitClass(Class target, TypeVariableMap arg) {
            }

            @Override
            protected void visitParameterized(ParameterizedType target, TypeVariableMap arg) {
                int index = 0;
                for (TypeVariable typeVariable : ((Class) target.getRawType()).getTypeParameters()) {
                    arg.put(typeVariable, target.getActualTypeArguments()[index++]);
                }
            }
        }.visit(new TypeVariableMap());
    }

    public static Type findTypeVariable(TypeVariable variable, TypeVariableMap localTypes) {
        Type result = localTypes.getVariableType(variable);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Type variable " + variable + " is unresolvable");
    }
}
