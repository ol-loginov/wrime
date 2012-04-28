package wrime.bytecode;

import wrime.WrimeException;
import wrime.reflect.ClassName;

import java.lang.reflect.Type;

public interface ExpressionStack {
    ExpressionScope current();

    ExpressionScope openScope();

    ExpressionScope closeScope();

    Type findClass(ClassName className);

    void addImport(String className);

    void addParameter(String parameterName, Type parameterType, String options) throws WrimeException;

    boolean inheritAttribute(String attribute);

    Type getFunctorType(String functor);
}
