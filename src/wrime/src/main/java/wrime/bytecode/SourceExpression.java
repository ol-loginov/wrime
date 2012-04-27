package wrime.bytecode;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import wrime.WrimeException;
import wrime.ast.ClassName;
import wrime.reflect.old.ParameterizedTypeImpl;
import wrime.util.FunctorName;
import wrime.util.ParameterName;

import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceExpression implements ExpressionStack {
    private final Stack<ExpressionScope> contextStack = new Stack<ExpressionScope>();
    private final ClassLoader classLoader;

    private final SourceExpressionListener listener;

    private List<String> imports = new ArrayList<String>();
    private Map<String, ParameterName> parameters = new HashMap<String, ParameterName>();
    private Map<String, FunctorName> functors;

    public SourceExpression(ClassLoader classLoader, Map<String, FunctorName> functors, SourceExpressionListener listener) {
        this.listener = listener;
        this.classLoader = classLoader;
        this.functors = functors;
        this.contextStack.push(new ExpressionScopeImpl(null) {
            @Override
            public Type getVarType(String name) {
                Type def = super.getVarType(name);
                if (def != null) {
                    return def;
                }
                ParameterName parameter = parameters.get(name);
                return parameter != null ? parameter.getType() : null;
            }
        });
    }

    public List<String> getImports() {
        return imports;
    }

    public Map<String, ParameterName> getParameters() {
        return parameters;
    }

    @Override
    public ExpressionScope openScope() {
        listener.scopeAdded();
        ExpressionScopeImpl child = new ExpressionScopeImpl(current());
        contextStack.push(child);
        return child;
    }

    @Override
    public ExpressionScope closeScope() {
        listener.scopeRemoved();
        return contextStack.pop();
    }

    @Override
    public ExpressionScope current() {
        return contextStack.peek();
    }

    @Override
    public Type getFunctorType(String functor) {
        FunctorName instance = functors.get(functor);
        return instance == null ? null : instance.getType();
    }

    @Override
    public Type findClass(ClassName className) {
        return findClassType(className);
    }

    public Type findClassType(ClassName className) {
        List<Type> typeParameters = new ArrayList<Type>();
        if (className.getGenericTypes() != null) {
            for (ClassName t : className.getGenericTypes()) {
                typeParameters.add(findClassType(t));
            }
        }

        String genericTypeName = className.getPackageName() + className.getClassName().getText();
        Type instance = tryClass(genericTypeName, typeParameters);
        if (instance != null) {
            return instance;
        }
        String classSelfName = getPublicClassName(genericTypeName);
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

    private String combinePackageAndClass(String imports, String classSelfName) {
        return StringUtils.trimTrailingCharacter(imports, '*') + classSelfName;
    }

    private String getPublicClassName(String name) {
        Matcher matcher = Pattern.compile("[^.]+$").matcher(name);
        if (matcher.find()) {
            return matcher.group();
        }
        return name;
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

    @Override
    public void addImport(String className) {
        imports.add(className);
    }

    @Override
    public void addParameter(String parameterName, Type parameterType, String option) throws WrimeException {
        if (!SourceComposer.isIdentifier(parameterName)) {
            SourceComposer.throwError("not a Java identifier " + parameterName);
        }
        if (parameters.containsKey(parameterName)) {
            SourceComposer.throwError("duplicate for model parameter " + parameterName);
        }
        parameters.put(parameterName, new ParameterName(parameterName, parameterType, option));
    }

    @Override
    public boolean inheritAttribute(String attribute) {
        List<ExpressionScope> list = new ArrayList<ExpressionScope>(contextStack);
        Collections.reverse(list);
        for (ExpressionScope ctx : list) {
            if (ctx.hasAttribute(attribute)) {
                return true;
            }
        }
        return false;
    }
}
