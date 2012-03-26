package wrime;

import org.junit.Test;
import wrime.functor.I18Nizer;

public class FunctorCallTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super.getEngine()
                .addFunctor("i18n", new I18Nizer());
    }

    @Test
    public void callContextFunction() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void callUnknownFunctor() throws WrimeException {
        parseWithError("001", "Expression analyser reports an error: unknown tag, variable or functor 'no_module' (FunctorCallTest/001.txt:1, column 3)");
    }
}
