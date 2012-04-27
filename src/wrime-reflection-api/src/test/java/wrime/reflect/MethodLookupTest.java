package wrime.reflect;

import org.junit.Test;
import wrime.reflect.model.ConcreteClass;
import wrime.reflect.model.OverrideClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MethodLookupTest {
    private void assertMethod(MethodDef method, String methodName, String returnTypeName) {
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        assertEquals(returnTypeName, Types.getJavaSourceName(method.getReturnType()));
    }

    @Test
    public void lookup() {
        // simple
        assertMethod(MethodLookup.findInvoker(ConcreteClass.class, "integer"), "getInteger", "int");
        assertMethod(MethodLookup.findInvoker(ConcreteClass.class, "integerRef"), "getIntegerRef", "java.lang.Integer");
        assertMethod(MethodLookup.findInvoker(ConcreteClass.class, "strings", String.class, String.class), "strings", "void");

        // overrides
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, String.class), "foo", "java.lang.String");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, Integer.class), "foo", "java.lang.Object");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, int.class), "foo", "java.lang.Object");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, Object.class), "foo", "void");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, Object.class, Object.class), "foo", "void");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, Object.class, int.class), "foo", "void");
        assertMethod(MethodLookup.findInvoker(OverrideClass.class, "foo", String.class, int.class, int.class), "foo", "void");
    }
}
