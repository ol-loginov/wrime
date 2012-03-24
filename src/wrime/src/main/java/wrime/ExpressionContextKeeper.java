package wrime;

import java.util.Collection;

public interface ExpressionContextKeeper {
    ExpressionContext current();

    Class findClass(String className);

    void addImport(String className);

    void addModelParameter(String parameterTypeDef, String parameterName, Class parameterClass, String options) throws WrimeException;

    Collection<ParameterName> getModelParameters();

    ExpressionContext openScope();

    ExpressionContext closeScope();

    TypeName findFunctorType(String name);
}
