package wrime;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import wrime.model.MapHolder;

import java.lang.reflect.Method;

public class ForSimpleTest extends TestClass {
    @Test
    public void emptyLoop() throws WrimeException {
        parseAndVerify("001");
    }

    @Test
    public void autoCast() throws WrimeException {
        parseAndVerify("002");
    }

    @Test
    public void iterateMap() throws WrimeException {
        Method me = BeanUtils.findMethod(MapHolder.MapInstance.class, "iterator");
        //me.getGenericReturnType()
        //me.getReturnType()
        parseAndVerify("003");
    }
}
