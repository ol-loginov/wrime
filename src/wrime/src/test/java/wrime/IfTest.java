package wrime;

import org.junit.Test;

public class IfTest extends TestClass {
    @Test
    public void justOne() throws WrimeException {
        parseAndVerify("000");
    }

    @Test
    public void voidInIf() throws WrimeException {
        parseWithError("001", "component is not of boolean type (needed for test expression) (IfTest/001.txt:2, column 10)");
    }

    @Test
    public void elses() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void elseWithoutIf() throws WrimeException {
        parseWithError("003", "Current scope is not FOR (IfTest/003.txt:2, column 7)");
    }

    @Test
    public void elifWithoutIf() throws WrimeException {
        parseWithError("004", "lexical error in command (IfTest/004.txt:1, column 1)");
    }

    @Test
    public void elifEmpty() throws WrimeException {
        parseWithError("005", "lexical error in command (IfTest/005.txt:2, column 1)");
    }
}
