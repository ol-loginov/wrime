package wrime.reflect;

import wrime.reflect.old.ParameterizedTypeImpl;
import wrime.reflect.old.TypeVariableMap;

import java.lang.reflect.*;

public class TypeUtil {
    public static Type getSuperclass(Type type) {
        if (Types.isClass(type)) {
            return ((Class) type).getGenericSuperclass();
        }
        if (Types.isParameterizedType(type)) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            assert Types.isClass(parameterizedType.getRawType());
            Type rawSuperclass = ((Class) parameterizedType.getRawType()).getGenericSuperclass();
            if (rawSuperclass == null) {
                return null;
            } else if (Types.isClass(rawSuperclass)) {
                return rawSuperclass;
            } else if (Types.isParameterizedType(rawSuperclass)) {
                ParameterizedType parameterizedSuperclass = (ParameterizedType) rawSuperclass;
                ParameterizedTypeImpl superClassImpl = new ParameterizedTypeImpl(parameterizedSuperclass);

                Type[] rawTypeParameters = parameterizedType.getActualTypeArguments();
                Type[] parameterizedSuperclassTypeParameters = parameterizedSuperclass.getActualTypeArguments();

                TypeVariableMap localMap = new TypeVariableMap();

                int rawTypeParametersIndex = 0;
                for (int i = 0; i < parameterizedSuperclassTypeParameters.length; ++i) {
                    if (Types.isClass(parameterizedSuperclassTypeParameters[i])) {
                        // use this as well
                    } else if (Types.isTypeVariable(parameterizedSuperclassTypeParameters[i])) {
                        TypeVariable variable = (TypeVariable) parameterizedSuperclassTypeParameters[i];
                        if (localMap.getVariableType(variable) == null) {
                            localMap.put(variable, rawTypeParameters[rawTypeParametersIndex++]);
                        }
                        parameterizedSuperclassTypeParameters[i] = localMap.getVariableType(variable);
                    } else {
                        throw new IllegalStateException("cannot convert to parameterized type argument");
                    }
                }

                superClassImpl.setTypeParameterArray(parameterizedSuperclassTypeParameters);
                return superClassImpl;
            }
        }
        throw new IllegalArgumentException("not implemented for " + type);
    }

    public static Type getComponentType(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        } else if (type instanceof Class) {
            return ((Class) type).getComponentType();
        } else {
            throw new IllegalStateException("unable to extract component type");
        }
    }

    public static Type getTypeParameterOf(Type type, Class typeVariableDeclaringClass, int index) {
        Type result = getTypeParameterOf0(type, typeVariableDeclaringClass, index);
        if (result == null) {
            throw new IllegalArgumentException("No " + Types.getJavaSourceName(typeVariableDeclaringClass) + " found in inheritance chain of " + Types.getJavaSourceName(type));
        }
        return result;
    }

    public static Type getTypeParameterOf0(Type type, final Class typeVariableDeclaringClass, final int index) {
        return new TypeVisitor<Type>(type) {
            @Override
            protected Type visitClass(Class target) {
                if (target == typeVariableDeclaringClass) {
                    return typeVariableDeclaringClass.getTypeParameters()[index];
                }
                Type result = findFromInterfaces(target);
                return result != null ? result : findFromSuperclass(target);
            }

            @Override
            protected Type visitParameterized(ParameterizedType target) {
                if (target.getRawType() == typeVariableDeclaringClass) {
                    return target.getActualTypeArguments()[index];
                }
                Type result = findFromInterfaces(target);
                return result != null ? result : findFromSuperclass(target);
            }

            private Type findFromInterfaces(Type target) {
                for (Type one : getInterfaces(target)) {
                    Type result = getTypeParameterOf0(one, typeVariableDeclaringClass, index);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }

            private Type findFromSuperclass(Type target) {
                Type superClass = getSuperclass(target);
                return superClass == null ? null : getTypeParameterOf0(superClass, typeVariableDeclaringClass, index);
            }
        }.visit();
    }

    public static Method[] getDeclaredMethods(Type type) {
        if (Types.isClass(type)) {
            return ((Class) type).getDeclaredMethods();
        }
        if (Types.isParameterizedType(type)) {
            return getDeclaredMethods(((ParameterizedType) type).getRawType());
        }
        throw new IllegalArgumentException("not implemented for " + type);
    }

    public static Type[] getInterfaces(Type type) {
        if (Types.isClass(type)) {
            return ((Class) type).getGenericInterfaces();
        }
        if (Types.isParameterizedType(type)) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            assert Types.isClass(parameterizedType.getRawType());

            Type[] genericInterfaces = ((Class) parameterizedType.getRawType()).getGenericInterfaces();
            for (int i = 0; i < genericInterfaces.length; ++i) {
                if (Types.isClass(genericInterfaces[i])) {
                    // just leave as is
                } else if (Types.isParameterizedType(genericInterfaces[i])) {
                    ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[i];
                    Type[] genericInterfaceTypeParameters = genericInterface.getActualTypeArguments();
                    int typeParametersIndex = 0;
                    for (int x = 0; x < genericInterfaceTypeParameters.length; ++x) {
                        if (Types.isClass(genericInterfaceTypeParameters[i])) {
                            // use this as well
                        } else if (Types.isTypeVariable(genericInterfaceTypeParameters[i])) {
                            genericInterfaceTypeParameters[i] = parameterizedType.getActualTypeArguments()[typeParametersIndex++];
                        } else {
                            throw new IllegalStateException("cannot convert to parameterized type argument");
                        }
                    }
                    ParameterizedTypeImpl superClassImpl = new ParameterizedTypeImpl(genericInterface);
                    superClassImpl.setTypeParameterArray(genericInterfaceTypeParameters);
                    genericInterfaces[i] = superClassImpl;
                } else {
                    throw new IllegalStateException("cannot extract interface list from parameterized type " + type);
                }
            }
            return genericInterfaces;
        }
        throw new IllegalArgumentException("not implemented for " + type);
    }
}
