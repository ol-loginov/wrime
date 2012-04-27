package wrime.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class TypeGcdFinder {
    public static List<Type> appendInheritance(List<Type> list, Type source) {
        if (Types.isClass(source)) {
            appendInheritance(list, (Class) source);
        } else if (Types.isParameterizedType(source)) {
            appendInheritance(list, (ParameterizedType) source);
        }
        return list;
    }

    private static void appendInheritance(List<Type> list, ParameterizedType source) {
        list.add(source);
        appendInheritance(list, source.getRawType());
    }

    private static void appendInheritance(List<Type> list, Class source) {
        list.add(source);
        appendInheritance(list, source.getGenericSuperclass());
        for (Type i : source.getGenericInterfaces()) {
            appendInheritance(list, i);
        }
    }

    public static void removeAll(List<Type> target, List<Type> types) {
    }
}
