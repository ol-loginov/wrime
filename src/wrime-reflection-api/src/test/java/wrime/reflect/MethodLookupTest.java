package wrime.reflect;

import org.junit.Test;
import wrime.reflect.model.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MethodLookupTest {
    private void assertMethod(MethodLookup method, String methodName, String returnTypeName) {
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        assertEquals(returnTypeName, Types.getJavaSourceName(method.getReturnType()));
    }

    @Test
    public void simple() {
        assertMethod(MethodLookuper.findInvoker(ConcreteClass.class, "integer"), "getInteger", "int");
        assertMethod(MethodLookuper.findInvoker(ConcreteClass.class, "integerRef"), "getIntegerRef", "java.lang.Integer");
        assertMethod(MethodLookuper.findInvoker(ConcreteClass.class, "strings", String.class, String.class), "strings", "void");
    }

    @Test
    public void overrides() {
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, String.class), "foo", "java.lang.String");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, Integer.class), "foo", "java.lang.Object");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, int.class), "foo", "java.lang.Object");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, Object.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, Object.class, Object.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, Object.class, int.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(OverrideClass.class, "foo", String.class, int.class, int.class), "foo", "void");
    }

    @Test(expected = IllegalArgumentException.class)
    public void genericMethods_Unresolvable() {
        assertMethod(MethodLookuper.findInvoker(GenericMethods.class, "unresolvable", int.class), "", "");
    }

    @Test
    public void genericMethods() {
        //assertMethod(MethodLookuper.findInvoker(GenericMethods.class, "foo", int.class), "foo", "java.lang.String");
        assertMethod(MethodLookuper.findInvoker(GenericMethods.class, "foo2", int.class), "foo2", "int");
        assertMethod(MethodLookuper.findInvoker(GenericMethods.class, "foo2", I1.class), "foo2", "wrime.reflect.model.I1");

        new GenericMethods().foo2(new I1Impl());
        new GenericMethods().foo2(Enum1.VAL1);
    }
}
