package wrime.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

class TypeUtil {
    public static TypeDef createReturnTypeDef(Method method) {
        return new TypeDef(method.getGenericReturnType());
    }

    private static boolean isCallableWithTypes(Method m, Type[] arguments) {
        Type[] parameters = m.getGenericParameterTypes();

        // test input length first
        if (!m.isVarArgs()) {
            if (arguments.length != parameters.length)
                return false;
        } else {
            if (arguments.length < parameters.length - 1)
                return false;
        }

        int substituteLength = m.isVarArgs() ? parameters.length - 1 : parameters.length;
        // now check parameters before potential vararg
        for (int idx = 0; idx < substituteLength; ++idx) {
            if (arguments[idx] == null) {
                // this means "null" as passed as argument
                continue;
            }
            if (!new TypeDef(parameters[idx]).isAssignableFrom(new TypeDef(arguments[idx]))) {
                return false;
            }
        }

        // first N parameters passes the check.
        // now check vararg sequence
        if (m.isVarArgs()) {
            TypeDef varType = new TypeDef(parameters[parameters.length - 1]).getComponentType();
            for (Type varArg : Arrays.copyOfRange(arguments, parameters.length - 1, arguments.length)) {
                if (varArg == null) {
                    // this means "null" as passed as argument
                    continue;
                }
                if (!varType.isAssignableFrom(new TypeDef(varArg))) {
                    return false;
                }
            }
        }

        // is this really bad
        return true;
    }

    private static Method findInvoker(TypeDescriptor invocable, String methodName, Type... argumentClasses) {
        throw new IllegalStateException("not implemented");
        /*
        for (Method m : invocable.getDeclaredMethods()) {
            if (!methodName.equals(m.getName())) {
                continue;
            }
            if (isCallableWithTypes(m, argumentClasses)) {
                return m;
            }
        }

        if (invocable.getSuperclass() != null) {
            return findInvoker(new TypeDef(invocable.getSuperclass()), methodName, argumentClasses);
        }
        return null;
        */
    }

    public static Method findMethodOrGetter2(TypeDef caller, String name, TypeDef... arguments) {
        throw new IllegalStateException("not implemented");
        /*
        if (arguments.length == 0) {
            String getterMethod = (caller.isBoolean() ? "is" : "get") + StringUtils.capitalize(name);
            try {
                if (caller.getType() instanceof Class) {
                    propDescriptor = BeanUtils.getPropertyDescriptor((Class) caller.getType(), name);
                }
            } catch (BeansException be) {
                propDescriptor = null;
            }

            if (propDescriptor != null) {
                return propDescriptor.getReadMethod();
            }
        }

        // no property found. try method then
        Type[] argumentClasses = new Type[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            argumentClasses[i] = arguments[i].getType();
        }
        return findInvoker(TypeDescriptorImpl.create(caller.getType()), name, argumentClasses);
        */
    }
}
