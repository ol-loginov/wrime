package wrime;

import org.junit.Test;
import wrime.functor.StringFunctor;

public class SetTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super.getEngine()
                .addFunctor("str", new StringFunctor());
    }

    @Test
    public void justOne() throws WrimeException {
        parseAndVerify("000");
    }
}
