package wrime;

import org.junit.Test;
import wrime.lang.TypeDef;
import wrime.model.MapHolder;

public class ForSimpleTest extends TestClass {
    @Test
    public void emptyLoop() throws WrimeException {
        parseAndVerify("001");
    }

    @Test
    public void autoCast() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void iterateMap() throws WrimeException {
        Object instance = new MapHolder<Integer>();
        TypeDef fff = new TypeDef(instance.getClass());
        parseAndVerify("003");
    }
}
