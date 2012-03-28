package wrime;

import org.junit.Test;
import wrime.functor.I18Nizer;
import wrime.functor.StringFunctor;

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
        parseWithError("012", "Expression analyser reports an error: cannot find suitable method with name 'call' (FunctionCallTest/012.txt:2, column 37)");
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
        parseWithError("014", "Expression analyser reports an error: no invocable at the point (FunctionCallTest/014.txt:2, column 16)");
    }
}
