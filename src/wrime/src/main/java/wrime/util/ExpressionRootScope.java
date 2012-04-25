package wrime.util;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import wrime.WrimeException;
import wrime.ast.ClassName;
import wrime.lang.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionRootScope implements ExpressionContextKeeper {
    private final Stack<ExpressionScope> contextStack;
    private final ClassLoader classLoader;
    private final ExpressionRuntime runtime;

    public ExpressionRootScope(ExpressionRuntime runtime, ClassLoader classLoader) {
        this.runtime = runtime;
        this.classLoader = classLoader;
        this.contextStack = new Stack<ExpressionScope>() {{
            push(new ExpressionChildScopeEx());
        }};
    }

    @Override
    public ExpressionScope openScope() {
        runtime.scopeAdded();
        ExpressionChildScope child = new ExpressionChildScope(current());
        contextStack.push(child);
        return child;
    }

    @Override
    public ExpressionScope closeScope() {
        runtime.scopeRemoved();
        return contextStack.pop();
    }

    @Override
    public ExpressionScope current() {
        return contextStack.peek();
    }

    @Override
    public TypeName getFunctorType(String functor) {
        FunctorName instance = runtime.getFunctor(functor);
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
        for (String imports : runtime.getImports()) {
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
        runtime.addImport(className);
    }

    @Override
    public void addParameter(String parameterName, Class parameterClass, String option) throws WrimeException {
        runtime.addParameter(parameterName, parameterClass, option);
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

    private class ExpressionChildScopeEx extends ExpressionChildScope {
        public ExpressionChildScopeEx() {
            super(null);
        }

        @Override
        public TypeName getVarType(String name) {
            TypeName def = super.getVarType(name);
            if (def != null) {
                return def;
            }
            ParameterName parameter = runtime.getModelParameter(name);
            if (parameter != null) {
                return parameter.getType();
            }
            return null;
        }
    }
}
