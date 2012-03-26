package wrime;

import org.junit.Test;

public class IfTest extends TestClass {
    @Test
    public void justOne() throws WrimeException {
        parseAndVerify("000");
    }

    @Test
    public void voidInIf() throws WrimeException {
        parseWithError("001", "IF builder reports an error: call is not conditional statement (IfTest/001.txt:2, column 21)");
    }

    @Test
    public void elses() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void elseWithoutIf() throws WrimeException {
        parseWithError("003", "ELSE builder reports an error: current scope is not IF scope (IfTest/003.txt:2, column 7)");
    }

    @Test
    public void elifWithoutIf() throws WrimeException {
        parseWithError("004", "ELIF builder reports an error: current scope is not IF scope (IfTest/004.txt:2, column 3)");
    }

    @Test
    public void elifEmpty() throws WrimeException {
        parseWithError("005", "ELIF builder reports an error: no condition specified (IfTest/005.txt:3, column 7)");
    }
}
