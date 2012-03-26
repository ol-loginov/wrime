package wrime;

import org.junit.Test;

public class IncludeSimpleTest extends TestClass {
    @Test
    public void empty() throws WrimeException {
        parseAndVerify("000");
    }

    @Test
    public void emptyWithTransient() throws WrimeException {
        parseAndVerify("001");
    }

    @Test
    public void emptyWithLocals() throws WrimeException {
        parseAndVerify("002");
    }
}
