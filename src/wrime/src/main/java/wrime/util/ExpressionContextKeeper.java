package wrime.util;

import wrime.WrimeException;
import wrime.ast.ClassName;
import wrime.lang.TypeName;

public interface ExpressionContextKeeper {
    ExpressionScope current();

    ExpressionScope openScope();

    ExpressionScope closeScope();

    Class findClass(ClassName className);

    void addImport(String className);

    void addParameter(String parameterName, Class parameterClass, String options) throws WrimeException;

    boolean inheritAttribute(String attribute);

    TypeName getFunctorType(String functor);
}
