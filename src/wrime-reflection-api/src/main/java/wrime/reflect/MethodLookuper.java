package wrime.reflect;

import org.springframework.util.StringUtils;
import wrime.reflect.old.ParameterizedTypeResolver;
import wrime.reflect.old.TypeVariableMap;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public abstract class MethodLookuper {
    public static MethodLookup findInvoker(Type caller, String name, Type... arguments) {
        if (arguments.length == 0) {
            String getterMethod = "is" + StringUtils.capitalize(name);
            try {
                return lookup(caller, getterMethod, arguments);
            } catch (NoSuchMethodException e) {
                // no method - ok
            }

            getterMethod = "get" + StringUtils.capitalize(name);
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
        List<MethodLookup> lookup = lookup0(target, 0, name, arguments);
        if (lookup.size() == 0) {
            throw new NoSuchMethodException("no suitable method found by name '" + name + "'");
        }

        if (lookup.size() == 1) {
            return lookup.get(0);
        }

        MethodLookup result = null;
        for (MethodLookup m : lookup) {
            if (m == null || m.getReturnType() == null) {
                throw new IllegalStateException("unable to resolve return type for method");
            }
            if (result == null) {
                result = m;
            } else {
                if (m.getWeight() == result.getWeight() && m.getDepth() == result.getDepth()) {
                    throw new IllegalStateException("Two methods found of same signature");
                }
                result = m.getWeight() > result.getWeight() ? m : result;
            }
        }
        return result;
    }

    private static List<MethodLookup> lookup0(Type target, int depth, String name, Type... arguments) throws NoSuchMethodException {
        List<MethodLookup> result = new ArrayList<MethodLookup>();

        Method[] targetMethods = TypeUtil.getDeclaredMethods(target);
        for (Method m : targetMethods) {
            if (!name.equals(m.getName())) {
                continue;
            }

            MethodLookup lookup = new MethodLookup(m, depth);
            if (hasSignatureMatch(target, lookup, arguments)) {
                result.add(lookup);
            }
        }

        target = TypeUtil.getSuperclass(target);
        if (target != null) {
            result.addAll(lookup0(target, depth + 1, name, arguments));
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

        Type[] methodParameters = lookup.method.getGenericParameterTypes();
        TypeVariableMap typeVariableMap = new TypeVariableMap();

        // first test - for non vararg parameter list
        int firstTestLength = methodParameters.length - (lookup.method.isVarArgs() ? 1 : 0);
        for (int i = 0; i < firstTestLength; ++i) {
            Type parameterType = methodParameters[i];
            if (Types.isTypeVariable(parameterType)) {
                TypeVariable parameterTypeVariable = (TypeVariable) parameterType;
                lookup.decrementWeight(TypeConverter.hasParameterizedBounds(parameterTypeVariable.getBounds()) ? MethodLookup.GENERIC_WITH_RESTRICTION : MethodLookup.GENERIC);
                if (!TypeConverter.passBounds(passedTypes[i], parameterTypeVariable.getBounds())) {
                    return false;
                }
                typeVariableMap.put((TypeVariable) parameterType, passedTypes[i]);
            } else if (!TypeConverter.isAssignable(parameterType, passedTypes[i])) {
                return false;
            }
        }

        // second test - for vararg parameter
        if (lookup.method.isVarArgs()) {
            // find vararg singe type
            Type varargType = TypeUtil.getComponentType(methodParameters[methodParameters.length - 1]);

            // this one will keep actual vararg type (from call signature)
            Type varargTypeResolved = varargType;

            Type[] gdcCandidates = Arrays.copyOfRange(passedTypes, firstTestLength, passedTypes.length);
            if (gdcCandidates.length == 0) {
                // in this case we just use component type as type variable if needed
            } else {
                // let's find GCD list for rest of parameters
                List<Type> gdcList = null;
                for (Type varArg : gdcCandidates) {
                    if (gdcList == null) {
                        gdcList = TypeGcdFinder.appendInheritance(new ArrayList<Type>(), varArg);
                    } else {
                        gdcList.retainAll(TypeGcdFinder.appendInheritance(new ArrayList<Type>(), varArg));
                    }
                }

                if (gdcList == null || gdcList.isEmpty()) {
                    return false;
                }

                // probably this type is specified already
                if (Types.isTypeVariable(varargTypeResolved)) {
                    Type varargTypeSpecified = typeVariableMap.getVariableType((TypeVariable) varargType);
                    if (varargTypeSpecified != null) {
                        varargTypeResolved = varargTypeSpecified;
                    }
                }

                // or we can select it from parameter list
                if (Types.isTypeVariable(varargTypeResolved)) {
                    Stack<Type> variableCandidates = new Stack<Type>();
                    variableCandidates.addAll(gdcList);
                    Collections.reverse(variableCandidates);

                    boolean varargTypeFound = false;
                    while (!varargTypeFound && !variableCandidates.empty()) {
                        Type nextCandidate = variableCandidates.pop();
                        if (TypeConverter.passBounds(nextCandidate, ((TypeVariable) varargTypeResolved).getBounds())) {
                            varargTypeResolved = nextCandidate;
                            varargTypeFound = true;
                        }
                    }

                    // if no candidate found - method is wrong
                    if (!varargTypeFound) {
                        return false;
                    }
                }

                if (Types.isClass(varargTypeResolved) && !gdcList.contains(varargTypeResolved)) {
                    return false;
                }
            }

            // but we still can have a typed variable there
            // if no parameters was given for
            if (Types.isTypeVariable(varargTypeResolved)) {
                typeVariableMap.put((TypeVariable) varargType, ((TypeVariable) varargTypeResolved).getBounds()[0]);
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
