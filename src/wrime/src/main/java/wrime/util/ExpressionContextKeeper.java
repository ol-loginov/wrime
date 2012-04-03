package wrime.util;

import wrime.WrimeException;

import java.util.Collection;

public interface ExpressionContextKeeper {
    ExpressionContextChild current();

    Class findClass(String className);

    void addImport(String className);

    void addModelParameter(String parameterTypeDef, String parameterName, Class parameterClass, String options) throws WrimeException;

    Collection<ParameterName> getModelParameters();

    ExpressionContextChild openScope();

    ExpressionContextChild closeScope();

    TypeName findFunctorType(String name);

    boolean inheritAttribute(String attribute);
}
