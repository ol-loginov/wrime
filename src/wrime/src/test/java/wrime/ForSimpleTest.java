package wrime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ForSimpleTest {
    private TestResource resources = new TestResource(ForSimpleTest.class);

    private void check(String resource) throws WrimeException {
        WrimeCompiler compiler = parse(resources.load(resource + ".txt"));
        resources.verify(resource + ".code", compiler.getClassCode());
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

    private WrimeCompiler parse(ScriptResource resource) throws WrimeException {
        return new WrimeEngine()
                .setOption(WrimeEngine.Scanner.EAT_SPACE, true)
                .parse(resource);
    }

    @Test
    public void emptyLoop() throws WrimeException {
        check("001");
    }

    @Test
    public void autoCast() throws WrimeException {
        check("002");
    }
}
