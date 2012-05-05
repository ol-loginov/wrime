package wrime.reflect;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import wrime.reflect.old.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeLocator {
    private final static Pattern PUBLIC_NAME_PATTERN = Pattern.compile("(.*\\.)([^.]+)$");
    private final ClassLoader classLoader;
    private final List<String> imports = new ArrayList<String>();

    public TypeLocator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<String> getImports() {
        return imports;
    }

    public Type findType(ClassName className) {
        return findClassType(className);
    }

    private Type findClassType(ClassName className) {
        List<Type> typeParameters = new ArrayList<Type>();
        if (className.getGenericTypes() != null) {
            for (ClassName t : className.getGenericTypes()) {
                typeParameters.add(findClassType(t));
            }
        }

        String genericPackage = className.getPackageName();
        if (genericPackage.length() > 0 && !genericPackage.endsWith(".")) {
            genericPackage += ".";
        }
        String genericTypeName = genericPackage + className.getClassName();
        Type instance = tryClass(genericTypeName, typeParameters);
        if (instance != null) {
            return instance;
        }
        String classSelfName = getClassName(genericTypeName);
        for (String imports : getImports()) {
            if (imports.endsWith("." + classSelfName)) {
                instance = tryClass(imports, typeParameters);
            }
            if (imports.endsWith(".*")) {
                instance = tryClass(combinePackageAndClass(imports, classSelfName), typeParameters);
            }
            if (instance != null) {
                return instance;
            }
        }
        throw new NoClassDefFoundError(className.toString());
    }

    private Type tryClass(String paramType, List<Type> typeParameters) {
        try {
            Class<?> genericClass = ClassUtils.forName(paramType, classLoader);
            if (genericClass == null) {
                return null;
            }
            if (typeParameters.isEmpty()) {
                return genericClass;
            } else {
                ParameterizedTypeImpl impl = new ParameterizedTypeImpl(genericClass);
                impl.setTypeParameterArray(typeParameters.toArray(new Type[typeParameters.size()]));
                return impl;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String combinePackageAndClass(String imports, String classSelfName) {
        return StringUtils.trimTrailingCharacter(imports, '*') + classSelfName;
    }

    public static String getClassName(String className) {
        Matcher matcher = PUBLIC_NAME_PATTERN.matcher(className);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return className;
    }

    public static String getClassNamePrefix(String className) {
        Matcher matcher = PUBLIC_NAME_PATTERN.matcher(className);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            return prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
        }
        return "";
    }

    public static String byteToCodeName(String name) {
        return name.replace("$", ".");
    }
}
