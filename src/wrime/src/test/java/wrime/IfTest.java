package wrime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IfTest {
    private TestResource resources = new TestResource(IfTest.class);

    private void check(String resource) throws WrimeException {
        WrimeCompiler compiler = parse(resources.load(resource + ".txt"));
        resources.verify(resource + ".code", compiler.getClassCode());
    }

    private WrimeCompiler parse(ScriptResource resource) throws WrimeException {
        return new WrimeEngine()
                .setOption(WrimeEngine.Scanner.EAT_SPACE, true)
                .parse(resource);
    }

    private void checkError(String resource, String message) {
        boolean caught = false;
        try {
            check(resource);
        } catch (WrimeException e) {
            caught = true;
            assertEquals(message, e.getMessage());
        }
        if (!caught) {
            fail("Exception expected");
        }
    }

    @Test
    public void justOne() throws WrimeException {
        check("000");
    }

    @Test
    public void voidInIf() throws WrimeException {
        checkError("001", "IF builder reports an error: call is not conditional statement (IfTest/001.txt:2, column 21)");
    }

    @Test
    public void elses() throws WrimeException {
        check("002");
    }

    @Test
    public void elseWithoutIf() throws WrimeException {
        checkError("003", "ELSE builder reports an error: current scope is not IF scope (IfTest/003.txt:2, column 7)");
    }

    @Test
    public void elifWithoutIf() throws WrimeException {
        checkError("004", "ELIF builder reports an error: current scope is not IF scope (IfTest/004.txt:2, column 3)");
    }

    @Test
    public void elifEmpty() throws WrimeException {
        checkError("005", "ELIF builder reports an error: no condition specified (IfTest/005.txt:3, column 7)");
    }
}
