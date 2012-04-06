package wrime;

import wrime.bytecode.SourceComposer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestClass {
    protected TestResource resources;

    public TestClass() {
        resources = new TestResource(getClass());
    }

    protected SourceComposer parse(ScriptResource resource) throws WrimeException {
        WrimeEngine engine = getEngine();
        try {
            return getEngine().compose(resource);
        } finally {
            engine.getRootPath().delete();
        }
    }

    protected WrimeEngine getEngine() throws WrimeException {
        WrimeEngineFactory factory = new WrimeEngineFactory() {
            @Override
            public void initializeFunctors(WrimeEngine engine) {
            }
        };
        return factory
                .create()
                .setOption(WrimeEngine.Scanner.EAT_SPACE, true);
    }

    protected void parseAndVerify(String resource) throws WrimeException {
        SourceComposer compiler = parse(resources.load(resource + ".txt"));
        try {
            resources.verify(resource + ".code", compiler.getClassCode());
        } catch (IOException e) {
            throw new WrimeException("i/o error", e);
        }
    }

    protected void renderAndVerify(String resource, Map<String, Object> model) throws WrimeException {
        WrimeEngine engine = getEngine();
        try {
            StringWriter result = new StringWriter();
            engine.render(resources.load(resource + ".txt"), result, model);
            resources.verify(resource + ".output", result.toString());
        } finally {
            engine.getRootPath().delete();
        }
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
