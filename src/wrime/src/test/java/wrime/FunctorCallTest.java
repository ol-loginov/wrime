package wrime;

import org.junit.Test;
import wrime.functor.I18Nizer;

public class FunctorCallTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super
                .getEngine()
                .registerFunctor("i18n", I18Nizer.class, new I18Nizer());
    }

    @Test
    public void callContextFunction() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void callUnknownFunctor() throws WrimeException {
        parseWithError("001", "No functor 'no_module' defined at a point (FunctorCallTest/001.txt:1, column 2)");
    }
}
