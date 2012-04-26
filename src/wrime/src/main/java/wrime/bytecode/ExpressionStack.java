package wrime.bytecode;

import wrime.WrimeException;
import wrime.ast.ClassName;
import wrime.lang.TypeDef;

public interface ExpressionStack {
    ExpressionScope current();

    ExpressionScope openScope();

    ExpressionScope closeScope();

    Class findClass(ClassName className);

    void addImport(String className);

    void addParameter(String parameterName, Class parameterClass, String options) throws WrimeException;

    boolean inheritAttribute(String attribute);

    TypeDef getFunctorType(String functor);
}
