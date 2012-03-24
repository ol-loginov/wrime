package wrime;

import org.junit.Ignore;
import org.junit.Test;
import wrime.functor.I18Nizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FunctionCallTest {
    private TestResource resources = new TestResource(FunctionCallTest.class);

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
                .addFunctor("i18n", new I18Nizer())
                .setOption(WrimeEngine.Scanner.EAT_SPACE, true)
                .parse(resource);
    }

    @Test
    public void vararg() throws WrimeException {
        check("013");
    }

    @Test
    public void argumentCountCheck() throws WrimeException {
        checkError("012", "Expression analyser reports an error: cannot find suitable method with name 'call' (FunctionCallTest/012.txt:2, column 37)");
    }

    @Test
    public void call2Arg() throws WrimeException {
        check("010");
    }

    @Test
    public void callNativeOverload() throws WrimeException {
        check("011");
    }

    @Test
    @Ignore
    public void callOnVoidResult() throws WrimeException {
        checkError("014", "Expression analyser reports an error: no invocable at the point (FunctionCallTest/014.txt:2, column 33)");
    }
}
