package wrime;

import org.junit.Test;
import wrime.functor.LogicFunctor;
import wrime.functor.StringFunctor;
import wrime.model.Bean2;
import wrime.model.ForIterator;
import wrime.model.IfBean;

import java.util.Map;
import java.util.TreeMap;

public class JavaCompileTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super
                .getEngine()
                .registerFunctor("str", StringFunctor.class, new StringFunctor())
                .registerFunctor("l", LogicFunctor.class, new LogicFunctor());
    }

    @Test
    public void allInOne() throws WrimeException {
        parseAndVerify("000");

        Map<String, Object> model = new TreeMap<String, Object>();
        model.put("a", "строка А");
        model.put("b2", new Bean2());
        model.put("bif", new IfBean());
        model.put("fir", new ForIterator());

        renderAndVerify("000", model);
    }

    @Test
    public void withInclude() throws WrimeException {
        parseAndVerify("001");

        Map<String, Object> model = new TreeMap<String, Object>();
        model.put("a", "строка А");

        renderAndVerify("001", model);
    }
}
