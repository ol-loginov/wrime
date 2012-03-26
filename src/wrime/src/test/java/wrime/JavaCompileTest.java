package wrime;

import org.junit.Test;
import wrime.functor.LogicFunctor;
import wrime.functor.StringFunctor;
import wrime.model.Bean2;
import wrime.model.ForIterator;
import wrime.model.IfBean;
import wrime.output.WrimeWriter;

import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertNotNull;

public class JavaCompileTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super.getEngine()
                .addFunctor("str", new StringFunctor())
                .addFunctor("l", new LogicFunctor());
    }

    @Test
    public void allInOne() throws WrimeException {
        parseAndVerify("000");

        StringWriter result = new StringWriter();
        WrimeWriter writer = getEngine().getWriter(resources.load("000.txt"), result);
        assertNotNull(writer);

        Map<String, Object> model = new TreeMap<String, Object>();
        model.put("a", "строка А");
        model.put("b2", new Bean2());
        model.put("bif", new IfBean());
        model.put("fir", new ForIterator());
        writer.render(model);
        resources.verify("000.output", result.toString());
    }
}
