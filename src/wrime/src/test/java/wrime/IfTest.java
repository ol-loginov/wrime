package wrime;

import org.junit.Test;

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

    @Test
    public void justOne() throws WrimeException {
        check("000");
    }
}
