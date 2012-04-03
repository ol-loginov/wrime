package wrime.util;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import wrime.WrimeException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionContextRoot extends ExpressionContextChild implements ExpressionContextKeeper {
    private final Stack<ExpressionContextChild> contextStack;
    private final ExpressionRuntime runtime;

    public ExpressionContextRoot(ExpressionRuntime runtime, ClassLoader classLoader) {
        super(classLoader);
        this.runtime = runtime;
        this.contextStack = new Stack<ExpressionContextChild>() {{
            push(ExpressionContextRoot.this);
        }};
    }

    @Override
    public ExpressionContextChild openScope() {
        runtime.scopeAdded();
        ExpressionContextChild child = new ExpressionContextChild(current(), getClassLoader());
        contextStack.push(child);
        return child;
    }

    @Override
    public ExpressionContextChild closeScope() {
        runtime.scopeRemoved();
        return contextStack.pop();
    }

    @Override
    public ExpressionContextChild current() {
        return contextStack.peek();
    }

    @Override
    public Class findClass(String className) {
        Class instance = tryClass(className);
        if (instance != null) {
            return instance;
        }
        String classSelfName = getPublicClassName(className);
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

    public Class tryClass(String paramType) {
        try {
            return ClassUtils.forName(paramType, getClassLoader());
        } catch (LinkageError e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void addImport(Class<?> className) {
        runtime.addImport(className.getName());
    }

    public void addImport(String className) {
        runtime.addImport(className);
    }

    @Override
    public void addModelParameter(String parameterTypeDef, String parameterName, Class parameterClass, String option) throws WrimeException {
        runtime.addModelParameter(parameterName, parameterTypeDef, parameterClass, option);
    }

    @Override
    public Collection<ParameterName> getModelParameters() {
        return runtime.getModelParameters();
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

    @Override
    public TypeName findFunctorType(String name) {
        return runtime.findFunctorType(name);
    }

    @Override
    public boolean inheritAttribute(String attribute) {
        List<ExpressionContextChild> list = new ArrayList<ExpressionContextChild>(contextStack);
        Collections.reverse(list);
        for (ExpressionContextChild ctx : list) {
            if (ctx.hasAttribute(attribute)) {
                return true;
            }
        }
        return false;
    }
}
