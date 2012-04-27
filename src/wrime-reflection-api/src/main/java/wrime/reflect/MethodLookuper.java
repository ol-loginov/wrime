package wrime.reflect;

import org.springframework.util.StringUtils;
import wrime.reflect.old.ParameterizedTypeResolver;
import wrime.reflect.old.TypeVariableMap;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MethodLookuper {
    public static MethodLookup findInvoker(Type caller, String name, Type... arguments) {
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

    private static MethodLookup lookup(Type target, String name, Type... arguments) throws NoSuchMethodException {
        List<MethodLookup> lookup = lookup0(target, name, arguments);
        if (lookup.size() == 0) {
            throw new NoSuchMethodException("no suitable method found by name '" + name + "'");
        }

        MethodLookup result = null;
        for (MethodLookup m : lookup) {
            if (m == null || m.getReturnType() == null) {
                throw new IllegalStateException("unable to resolve return type for method");
            }
            if (result == null) {
                result = m;
            } else {
                if (m.getWeight() == result.getWeight()) {
                    throw new IllegalStateException("Two methods found of same signature");
                }
                result = m.getWeight() > result.getWeight() ? m : result;
            }
        }
        return result;
    }

    private static List<MethodLookup> lookup0(Type target, String name, Type... arguments) throws NoSuchMethodException {
        List<MethodLookup> result = new ArrayList<MethodLookup>();

        Method[] targetMethods = TypeUtil.getDeclaredMethods(target);
        for (Method m : targetMethods) {
            if (!name.equals(m.getName())) {
                continue;
            }

            MethodLookup lookup = new MethodLookup(m);
            if (hasSignatureMatch(target, lookup, arguments)) {
                result.add(lookup);
            }
        }

        target = TypeUtil.getSuperclass(target);
        if (target != null) {
            result.addAll(lookup0(target, name, arguments));
        }

        return result;
    }

    private static boolean hasSignatureMatch(Type target, MethodLookup lookup, Type[] passedTypes) {
        lookup.setWeight(MethodLookup.NORMAL);

        // method have at least the same number of arguments
        if (lookup.method.isVarArgs()) {
            lookup.setWeight(MethodLookup.VARARG);
            if (passedTypes.length < lookup.method.getGenericParameterTypes().length - 1) {
                return false;
            }
        } else {
            if (passedTypes.length != lookup.method.getGenericParameterTypes().length) {
                return false;
            }
        }

        TypeVariableMap typeVariableMap = new TypeVariableMap();

        // first test - for non vararg parameter list
        int firstTestLength = lookup.method.getGenericParameterTypes().length - (lookup.method.isVarArgs() ? 1 : 0);
        for (int i = 0; i < firstTestLength; ++i) {
            Type parameterType = lookup.method.getGenericParameterTypes()[i];
            if (Types.isTypeVariable(parameterType)) {
                TypeVariable parameterTypeVariable = (TypeVariable) parameterType;
                lookup.decrementWeight(TypeConverter.hasParameterizedBounds(parameterTypeVariable.getBounds()) ? MethodLookup.GENERIC_WITH_RESTRICTION : MethodLookup.GENERIC);
                if (!TypeConverter.isInBounds(passedTypes[i], parameterTypeVariable.getBounds())) {
                    return false;
                }
                typeVariableMap.add(parameterType, passedTypes[i]);
            } else if (!TypeConverter.isAssignable(parameterType, passedTypes[i])) {
                return false;
            }
        }

        // second test - for vararg parameter
        if (lookup.method.isVarArgs()) {
            // let's find GCD list for rest of parameters
            List<Type> gdcList = null;
            for (Type varArg : Arrays.copyOfRange(passedTypes, firstTestLength, passedTypes.length)) {
                if (gdcList == null) {
                    gdcList = TypeGcdFinder.appendInheritance(new ArrayList<Type>(), varArg);
                } else {
                    TypeGcdFinder.removeAll(gdcList, TypeGcdFinder.appendInheritance(new ArrayList<Type>(), varArg));
                }
            }
        }

        Type returnType = lookup.method.getGenericReturnType();
        if (Types.isClass(returnType)) {
            lookup.setReturnType(returnType);
            return true;
        }

        if (Types.isParameterizedType(returnType)) {
            Type parameterizedType = ParameterizedTypeResolver.resolveType((ParameterizedType) returnType, typeVariableMap, target);
            lookup.setReturnType(parameterizedType);
            return true;
        }

        if (Types.isTypeVariable(returnType)) {
            Type parameterizedType = ParameterizedTypeResolver.findTypeVariable((TypeVariable) returnType, typeVariableMap, target);
            lookup.setReturnType(parameterizedType);
            return true;
        }

        return false;
    }
}
