package wrime.lang;

import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public abstract class MethodLookup {
    public static MethodDef findInvoker(TypeDef caller, String name, TypeDescriptor... arguments) {
        if (arguments.length == 0) {
            String getterMethod = (caller.isBoolean() ? "is" : "get") + StringUtils.capitalize(name);

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

    private static MethodDef lookup(TypeDescriptor target, String name, TypeDescriptor... arguments) throws NoSuchMethodException {
        Method[] targetMethods = target.getDeclaredMethods();
        for (Method m : targetMethods) {
            if (!name.equals(m.getName())) {
                continue;
            }

            MethodDef def = new MethodDef(m);
            if (hasSignatureMatch(def, arguments)) {
                return def;
            }
        }

        target = target.getSuperclass();
        if (target != null) {
            return lookup(target, name, arguments);
        }

        throw new NoSuchMethodException("no suitable method found by name '" + name + "'");
    }

    private static boolean hasSignatureMatch(MethodDef def, TypeDescriptor[] arguments) {
        Type[] defParameters = def.method.getGenericParameterTypes();

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
            if (arguments[idx] == null) {
                // this means "null" as passed as argument
                continue;
            }
            if (!new TypeDef(defParameters[idx]).isAssignableFrom(new TypeDef(arguments[idx].getType()))) {
                return false;
            }
        }

        // first N parameters passes the check.
        // now check vararg sequence
        if (def.method.isVarArgs()) {
            TypeDef varType = new TypeDef(defParameters[defParameters.length - 1]).getComponentType();
            for (TypeDescriptor varArg : Arrays.copyOfRange(arguments, defParameters.length - 1, arguments.length)) {
                if (varArg == null) {
                    // this means "null" as passed as argument
                    continue;
                }
                if (!varType.isAssignableFrom(new TypeDef(varArg.getType()))) {
                    return false;
                }
            }
        }

        Type returnType = def.method.getGenericReturnType();
        if (TypeDescriptorImpl.isClass(returnType)) {
            def.setReturnType(new TypeDef(returnType));
        } else if (TypeDescriptorImpl.isParameterizedType(returnType)) {
            ParameterizedType parameterizedReturnType = (ParameterizedType) returnType;
            Type[] array = parameterizedReturnType.getActualTypeArguments();
            def.setReturnType(new TypeDef(returnType));
        } else if (TypeDescriptorImpl.isTypeVariable(returnType)) {
            TypeVariable returnTypeVariable = (TypeVariable) returnType;
            // find argument with same name
            String argumentToAssign = returnTypeVariable.getName();

            for (int i = 0; i < defParameters.length; ++i) {
                if (!TypeDescriptorImpl.isTypeVariable(defParameters[i])) {
                    continue;
                }
                TypeVariable defParameterTypeVariable = (TypeVariable) defParameters[i];
                if (argumentToAssign.equals(defParameterTypeVariable.getName())) {
                    def.setReturnType(new TypeDef(arguments[i].getType()));
                    break;
                }
            }
        }

        if (def.getReturnType() == null) {
            throw new IllegalStateException("unable to resolve return type for method");
        }

        // is this really bad
        return true;
    }
}
