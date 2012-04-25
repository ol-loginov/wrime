package wrime.lang;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

public class TypeUtil {
    public static TypeName createReturnTypeDef(Method method) {
        return new TypeName(method.getGenericReturnType());
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
            if (!TypeWrap.create(parameters[idx]).isAssignableFrom(arguments[idx])) {
                return false;
            }
        }

        // first N parameters passes the check.
        // now check vararg sequence
        if (m.isVarArgs()) {
            Type varType = TypeWrap.create(parameters[parameters.length - 1]).getComponentType();
            for (Type varArg : Arrays.copyOfRange(arguments, parameters.length - 1, arguments.length)) {
                if (varArg == null) {
                    // this means "null" as passed as argument
                    continue;
                }
                if (!TypeWrap.create(varType).isAssignableFrom(varArg)) {
                    return false;
                }
            }
        }

        // is this really bad
        return true;
    }

    private static Method findInvoker(TypeWrap invocable, String methodName, Type... argumentClasses) {
        for (Method m : invocable.getDeclaredMethods()) {
            if (!methodName.equals(m.getName())) {
                continue;
            }
            if (isCallableWithTypes(m, argumentClasses)) {
                return m;
            }
        }

        if (invocable.getSuperclass() != null) {
            return findInvoker(TypeWrap.create(invocable.getSuperclass()), methodName, argumentClasses);
        }
        return null;
    }

    public static Method findMethodOrGetter(TypeName caller, String name, TypeName... arguments) {
        PropertyDescriptor propDescriptor = null;
        if (arguments.length == 0) {
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
        return findInvoker(TypeWrap.create(caller.getType()), name, argumentClasses);
    }
}
