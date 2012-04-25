package wrime.util;

import wrime.WrimeException;

import java.util.Collection;

public interface ExpressionRuntime {
    void scopeAdded();

    void scopeRemoved();

    ParameterName getModelParameter(String name);

    void addImport(String clazz);

    void addParameter(String parameterName, Class parameterClass, String option) throws WrimeException;

    Collection<ParameterName> getModelParameters();

    Collection<String> getImports();

    FunctorName getFunctor(String name);
}
