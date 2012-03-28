package wrime.util;

import wrime.WrimeException;

import java.util.Collection;

public interface ExpressionRuntime {
    void scopeAdded();

    void scopeRemoved();

    TypeName findFunctorType(String name);

    ParameterName getModelParameter(String name);

    void addImport(String clazz);

    void addModelParameter(String parameterName, String parameterTypeDef, Class parameterClass, String option) throws WrimeException;

    Collection<ParameterName> getModelParameters();

    Collection<String> getImports();
}
