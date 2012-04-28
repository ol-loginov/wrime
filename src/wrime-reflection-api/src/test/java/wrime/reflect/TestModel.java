package wrime.reflect;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class TestModel {
    private static final TypeLocator typeLocator = new TypeLocator(MethodLookupTest.class.getClassLoader());

    public static List<String> IMPORTS;

    static {
        IMPORTS = Arrays.asList(
                "java.lang.*",
                "java.util.*"
        );
    }

    public static Type typeOf(Class type, ClassName... typeParameters) {
        return typeLocator.findType(nameOf(type, typeParameters));
    }

    public static ClassName nameOf(Class type, ClassName... typeParameters) {
        ClassName name = new ClassName();
        name.setClassName(type.getSimpleName());
        if (type.getPackage() != null) {
            name.setPackageName(type.getPackage().getName());
        }
        name.setGenericTypes(Arrays.asList(typeParameters));
        return name;
    }
}
