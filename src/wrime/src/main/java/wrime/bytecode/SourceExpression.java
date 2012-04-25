package wrime.bytecode;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import wrime.WrimeException;
import wrime.ast.ClassName;
import wrime.lang.TypeName;
import wrime.util.FunctorName;
import wrime.util.ParameterName;

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
            public TypeName getVarType(String name) {
                TypeName def = super.getVarType(name);
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
    public TypeName getFunctorType(String functor) {
        FunctorName instance = functors.get(functor);
        return instance == null ? null : new TypeName(instance.getType());
    }

    @Override
    public Class findClass(ClassName className) {
        String genericTypeName = className.getPackageName() + className.getClassName().getText();
        Class instance = tryClass(genericTypeName);
        if (instance != null) {
            return instance;
        }
        String classSelfName = getPublicClassName(genericTypeName);
        for (String imports : getImports()) {
            if (imports.endsWith("." + classSelfName)) {
                return tryClass(imports);
            }
            if (imports.endsWith(".*")) {
                instance = tryClass(combinePackageAndClass(imports, classSelfName));
                if (instance == null) {
                    continue;
                }
                // this should work with situation:
                // imports has "java.*";
                // className is "lang.String";
                if (imports.equals(instance.getPackage().getName() + ".*")) {
                    return instance;
                }
            }
        }
        return null;
    }

    private String combinePackageAndClass(String imports, String classSelfName) {
        return StringUtils.trimTrailingCharacter(imports, '*') + classSelfName;
    }

    private String getPublicClassName(String name) {
        Matcher matcher = Pattern.compile("[^.]+").matcher(name);
        if (matcher.find()) {
            return matcher.group();
        }
        return name;
    }

    private Class tryClass(String paramType) {
        try {
            return ClassUtils.forName(paramType, classLoader);
        } catch (LinkageError e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void addImport(String className) {
        imports.add(className);
    }

    @Override
    public void addParameter(String parameterName, Class parameterClass, String option) throws WrimeException {
        if (!SourceComposer.isIdentifier(parameterName)) {
            SourceComposer.throwError("not a Java identifier " + parameterName);
        }
        if (parameters.containsKey(parameterName)) {
            SourceComposer.throwError("duplicate for model parameter " + parameterName);
        }
        TypeName parameterType = new TypeName(parameterClass);
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
