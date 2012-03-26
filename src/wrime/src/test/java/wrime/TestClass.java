package wrime;

import wrime.config.WrimeConfiguration;
import wrime.scanner.WrimeCompiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestClass {
    protected TestResource resources;

    public TestClass() {
        resources = new TestResource(getClass());
    }

    protected WrimeCompiler parse(ScriptResource resource) throws WrimeException {
        return getEngine()
                .parse(resource);
    }

    protected WrimeEngine getEngine() throws WrimeException {
        WrimeConfiguration config = new WrimeConfiguration() {
            @Override
            public void setFunctors(WrimeEngine engine) {
            }
        };
        return new WrimeEngine(config)
                .setOption(WrimeEngine.Scanner.EAT_SPACE, true);
    }

    protected void parseAndVerify(String resource) throws WrimeException {
        WrimeCompiler compiler = parse(resources.load(resource + ".txt"));
        resources.verify(resource + ".code", compiler.getClassCode());
    }

    protected void parseWithError(String resource, String message) {
        boolean caught = false;
        try {
            parseAndVerify(resource);
        } catch (WrimeException e) {
            caught = true;
            assertEquals(message, e.getMessage());
        }
        if (!caught) {
            fail("Exception expected");
        }
    }

}