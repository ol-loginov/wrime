package wrime;

import org.junit.Test;
import wrime.functor.StringFunctor;

import java.util.TreeMap;

public class SetTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super
                .getEngine()
                .setFunctors(new TreeMap<String, Object>() {{
                    put("str", new StringFunctor());
                }});
    }

    @Test
    public void justOne() throws WrimeException {
        parseAndVerify("000");
    }
}
