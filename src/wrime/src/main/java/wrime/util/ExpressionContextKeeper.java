package wrime.util;

import wrime.WrimeException;
import wrime.ast.ClassName;

import java.util.Collection;

public interface ExpressionContextKeeper {
    ExpressionContextChild current();

    Class findClass(ClassName className);

    void addImport(String className);

    void addModelParameter(String parameterTypeDef, String parameterName, Class parameterClass, String options) throws WrimeException;

    Collection<ParameterName> getModelParameters();

    ExpressionContextChild openScope();

    ExpressionContextChild closeScope();

    TypeName getFunctorType(String name);

    boolean inheritAttribute(String attribute);
}
