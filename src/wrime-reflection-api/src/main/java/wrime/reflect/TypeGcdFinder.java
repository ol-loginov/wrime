package wrime.reflect;

import java.lang.reflect.Type;
import java.util.List;

public class TypeGcdFinder {
    public static List<Type> appendInheritance(List<Type> list, Type source) {
        if (source != null) {
            if (Types.isClass(source) || Types.isParameterizedType(source)) {
                appendInheritanceWithInterfaces(list, source);
            } else {
                throw new IllegalStateException("not implemented");
            }
        }
        return list;
    }

    private static void appendInheritanceWithInterfaces(List<Type> list, Type source) {
        list.add(source);

        if (Types.isClass(source) && ((Class) source).isPrimitive()) {
            list.add(Object.class);
        }

        appendInheritance(list, TypeUtil.getSuperclass(source));
        for (Type i : TypeUtil.getInterfaces(source)) {
            appendInheritance(list, i);
        }
    }
}
