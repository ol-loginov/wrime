package wrime;

import org.junit.Test;

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
        parseAndVerify("003");
    }
}
