package wrime;

import org.junit.Test;
import wrime.functor.I18Nizer;

import java.util.TreeMap;

public class FunctionCallTest extends TestClass {
    @Override
    protected WrimeEngine getEngine() throws WrimeException {
        return super
                .getEngine()
                .setFunctors(new TreeMap<String, Object>() {{
                    put("i18n", new I18Nizer());
                }});
    }

    @Test
    public void vararg() throws WrimeException {
        parseAndVerify("013");
    }

    @Test
    public void argumentCountCheck() throws WrimeException {
        parseWithError("012", "No suitable method 'call' found in type wrime.model.Bean2 (FunctionCallTest/012.txt:2, column 7)");
    }

    @Test
    public void call2Arg() throws WrimeException {
        parseAndVerify("010");
    }

    @Test
    public void callNativeOverload() throws WrimeException {
        parseAndVerify("011");
    }

    @Test
    public void callOnVoidResult() throws WrimeException {
        parseWithError("014", "No suitable method 'no_getter' found in type void (FunctionCallTest/014.txt:2, column 15)");
    }
}
