package wrime.bytecode;

import wrime.WrimeException;
import wrime.reflect.ClassName;
import wrime.reflect.TypeLocator;
import wrime.util.FunctorName;
import wrime.util.ParameterName;

import java.lang.reflect.Type;
import java.util.*;

public class SourceExpression implements ExpressionStack {
    private final Stack<ExpressionScope> contextStack = new Stack<ExpressionScope>();

    private final SourceExpressionListener listener;
    private final TypeLocator typeLocator;

    private final Map<String, ParameterName> parameters = new HashMap<String, ParameterName>();
    private final Map<String, FunctorName> functors;

    public SourceExpression(ClassLoader classLoader, Map<String, FunctorName> functors, SourceExpressionListener listener) {
        this.typeLocator = new TypeLocator(classLoader);
        this.listener = listener;
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
        return typeLocator.getImports();
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
        return typeLocator.findType(className);
    }

    @Override
    public void addImport(String className) {
        getImports().add(className);
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
