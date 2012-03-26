package wrime;

import org.junit.Test;
import wrime.scanner.WrimeScannerDumper;

import java.io.StringWriter;

public class SimpleScannerTest {
    protected TestResource resources = new TestResource(SimpleScannerTest.class);

    private String parse(ScriptResource resource) throws WrimeException {
        StringWriter result = new StringWriter();
        WrimeEngine engine = new WrimeEngine();
        engine.scan(resource, new WrimeScannerDumper(result));
        return result.toString();
    }

    @Test
    public void emptyTemplate() throws Exception {
        String result = parse(resources.load("000.html"));
        resources.verify("000.html.scanner", result);
    }

    @Test
    public void oneVar() throws Exception {
        String result = parse(resources.load("001.html"));
        resources.verify("001.html.scanner", result);
    }

    @Test
    public void e003() throws Exception {
        String result = parse(resources.load("003.txt"));
        resources.verify("003.txt.scanner", result);
    }

    @Test
    public void e004() throws Exception {
        String result = parse(resources.load("004.txt"));
        resources.verify("004.txt.scanner", result);
    }

    @Test
    public void e005() throws Exception {
        String result = parse(resources.load("005.txt"));
        resources.verify("005.txt.scanner", result);
    }
}
