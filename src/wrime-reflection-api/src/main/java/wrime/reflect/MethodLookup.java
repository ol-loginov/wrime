package wrime.reflect;

import org.springframework.util.StringUtils;
import wrime.reflect.old.ParameterizedTypeMap;
import wrime.reflect.old.ParameterizedTypeResolver;

import java.lang.reflect.*;
import java.util.Arrays;

public abstract class MethodLookup {
    public static MethodDef findInvoker(Type caller, String name, Type... arguments) {
        if (arguments.length == 0) {
            String getterMethod = (Types.isBoolean(caller) ? "is" : "get") + StringUtils.capitalize(name);

            try {
                return lookup(caller, getterMethod, arguments);
            } catch (NoSuchMethodException e) {
                // no method - ok
            }
        }
        try {
            return lookup(caller, name, arguments);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static MethodDef lookup(Type target, String name, Type... arguments) throws NoSuchMethodException {
        MethodDef def = lookup0(target, name, arguments);
        if (def != null && def.getReturnType() == null) {
            throw new IllegalStateException("unable to resolve return type for method");
        }
        return def;
    }

    private static MethodDef lookup0(Type target, String name, Type... arguments) throws NoSuchMethodException {
        Method[] targetMethods = TypeUtil.getDeclaredMethods(target);
        for (Method m : targetMethods) {
            if (!name.equals(m.getName())) {
                continue;
            }

            MethodDef def = new MethodDef(m);
            if (hasSignatureMatch(target, def, arguments)) {
                return def;
            }
        }

        target = TypeUtil.getSuperclass(target);
        if (target != null) {
            return lookup(target, name, arguments);
        }

        throw new NoSuchMethodException("no suitable method found by name '" + name + "'");
    }

    private static boolean hasSignatureMatch(Type target, MethodDef def, Type[] arguments) {
        // method have at least the same number of arguments
        if (arguments.length < def.method.getGenericParameterTypes().length - 1) {
            return false;
        }

        ParameterizedTypeMap map = new ParameterizedTypeMap();

        // init type variables map
        Type[] defParameters = def.method.getGenericParameterTypes();
        for (int i = 0; i < defParameters.length; ++i) {
            if (arguments.length > i) {
                map.add(defParameters[i], arguments[i]);
            } else {
                map.add(defParameters[i], null);
            }
        }

        // test input length first
        if (!def.method.isVarArgs()) {
            if (arguments.length != defParameters.length)
                return false;
        } else {
            if (arguments.length < defParameters.length - 1)
                return false;
        }

        int substituteLength = def.method.isVarArgs() ? defParameters.length - 1 : defParameters.length;
        // now check parameters before potential vararg
        for (int idx = 0; idx < substituteLength; ++idx) {
            if (arguments[idx] == Types.NULL_TYPE) {
                // this means "null" as passed as argument
                continue;
            }
            if (!TypeConverter.isAssignable(defParameters[idx], arguments[idx])) {
                return false;
            }
        }

        // first N parameters passes the check.
        // now check vararg sequence
        if (def.method.isVarArgs()) {
            Type arrayType = defParameters[defParameters.length - 1];
            Type arrayItemType;
            if (arrayType instanceof GenericArrayType) {
                arrayItemType = ((GenericArrayType) arrayType).getGenericComponentType();
            } else if (arrayType instanceof Class) {
                arrayItemType = ((Class) arrayType).getComponentType();
            } else {
                throw new IllegalStateException("cannot resolve vararg target type");
            }

            for (Type varArg : Arrays.copyOfRange(arguments, defParameters.length - 1, arguments.length)) {
                if (varArg == null) {
                    // this means "null" as passed as argument
                    continue;
                }
                if (!TypeConverter.isAssignable(arrayItemType, varArg)) {
                    return false;
                }
            }
        }

        Type returnType = def.method.getGenericReturnType();
        if (Types.isClass(returnType)) {
            def.setReturnType(returnType);
            return true;
        }

        if (Types.isParameterizedType(returnType)) {
            ParameterizedType parameterizedType = ParameterizedTypeResolver.resolveType((ParameterizedType) returnType, map, target);
            def.setReturnType(parameterizedType);
            return true;
        }

        if (Types.isTypeVariable(returnType)) {
            TypeVariable returnTypeVariable = (TypeVariable) returnType;
            // find argument with same name
            String argumentToAssign = returnTypeVariable.getName();

            for (int i = 0; i < defParameters.length; ++i) {
                if (!Types.isTypeVariable(defParameters[i])) {
                    continue;
                }
                TypeVariable defParameterTypeVariable = (TypeVariable) defParameters[i];
                if (argumentToAssign.equals(defParameterTypeVariable.getName())) {
                    def.setReturnType(arguments[i]);
                    break;
                }
            }
        }

        return false;
    }
}
