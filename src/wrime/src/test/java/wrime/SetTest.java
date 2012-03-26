package wrime;

import org.junit.Test;

public class SetTest extends TestClass {
    @Test
    public void justOne() throws WrimeException {
        parseAndVerify("000");
    }
}
