package wrime;

import org.junit.Test;

public class SimpleCompilerTest extends TestClass {
    @Test
    public void empty() throws WrimeException {
        parseAndVerify("005");
    }

    @Test
    public void defineOneModelParameter() throws WrimeException {
        parseAndVerify("006");
    }

    @Test
    public void oneModelParameter() throws WrimeException {
        parseAndVerify("007");
    }

    @Test
    public void simpleWithCall() throws WrimeException {
        parseAndVerify("008");
    }

    @Test
    public void nestedProp() throws WrimeException {
        parseAndVerify("009");
    }
}

