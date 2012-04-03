package wrime.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import wrime.ops.Getter;
import wrime.ops.Invoker;
import wrime.ops.Operand;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

public class TypeUtil {
    private static TypeName createReturnTypeDef(Method method) {
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
            if (!TypeWrap.create(parameters[idx]).isAssignableFrom(arguments[idx])) {
                return false;
            }
        }

        // first N parameters passes the check.
        // now check vararg sequence
        if (m.isVarArgs()) {
            Type varType = TypeWrap.create(parameters[parameters.length - 1]).getComponentType();
            for (Type varArg : Arrays.copyOfRange(arguments, parameters.length - 1, arguments.length)) {
                if (!TypeWrap.create(varType).isAssignableFrom(varArg)) {
                    return false;
                }
            }
        }

        // is this really bad
        return true;
    }

    private static Getter createGetter(String name, PropertyDescriptor descriptor) {
        Getter getter = new Getter();
        getter.setPropName(name);
        getter.setPropMethod(descriptor.getReadMethod());
        getter.setResult(createReturnTypeDef(getter.getPropMethod()));
        return getter;
    }

    private static Invoker createInvoker(String name, Method method) {
        Invoker invoker = new Invoker();
        invoker.setMethodName(name);
        invoker.setMethod(method);
        if (method != null) {
            invoker.setResult(createReturnTypeDef(method));
        }
        return invoker;
    }


    public static Invoker findInvoker(TypeName invocable, String methodName, TypeName... argumentTypes) {
        Type[] argumentClasses = new Type[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; ++i) {
            argumentClasses[i] = argumentTypes[i].getType();
        }
        return findInvoker(TypeWrap.create(invocable.getType()), methodName, argumentClasses);
    }

    private static Invoker findInvoker(TypeWrap invocable, String methodName, Type... argumentClasses) {
        for (Method m : invocable.getDeclaredMethods()) {
            if (!methodName.equals(m.getName())) {
                continue;
            }
            if (isCallableWithTypes(m, argumentClasses)) {
                return createInvoker(methodName, m);
            }
        }

        if (invocable.getSuperclass() != null) {
            return findInvoker(TypeWrap.create(invocable.getSuperclass()), methodName, argumentClasses);
        }
        return null;
    }

    public static Operand findAnyInvokerOrGetter(TypeName typeDef, String name) {
        PropertyDescriptor propDescriptor = null;
        try {
            if (typeDef.getType() instanceof Class) {
                propDescriptor = BeanUtils.getPropertyDescriptor((Class) typeDef.getType(), name);
            }
        } catch (BeansException be) {
            propDescriptor = null;
        }

        if (propDescriptor != null) {
            return createGetter(name, propDescriptor);
        }

        // no property found. try method then

        Method method = null;
        try {
            if (typeDef.getType() instanceof Class) {
                method = BeanUtils.findDeclaredMethodWithMinimalParameters((Class) typeDef.getType(), name);
            }
        } catch (IllegalArgumentException ie) {
            return createInvoker(name, null);
        }

        return method != null ? createInvoker(name, method) : null;
    }
}
