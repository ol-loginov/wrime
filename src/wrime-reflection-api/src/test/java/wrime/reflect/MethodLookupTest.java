package wrime.reflect;

import org.junit.Assert;
import org.junit.Test;
import wrime.reflect.model.*;

import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static wrime.reflect.TestModel.nameOf;
import static wrime.reflect.TestModel.typeOf;

public class MethodLookupTest {
    private void assertMethod(String methodName, String returnTypeName, Type caller, String name, Type... arguments) {
        assertMethod(MethodLookuper.findInvoker(caller, name, arguments), methodName, returnTypeName);
    }

    private void assertNoMethod(Type caller, String name, Type... arguments) {
        Assert.assertNull("method was found occasionally", MethodLookuper.findInvoker(caller, name, arguments));
    }

    private void assertNoMethod(String message, Type caller, String name, Type... arguments) {
        try {
            MethodLookuper.findInvoker(caller, name, arguments);
        } catch (IllegalArgumentException e) {
            assertEquals(message, e.getMessage());
            return;
        }
        Assert.fail("method was found occasionally ");
    }

    private void assertMethod(MethodLookup method, String methodName, String returnTypeName) {
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        assertEquals(returnTypeName, Types.getJavaSourceName(method.getReturnType(), TestModel.IMPORTS));
    }

    @Test
    public void simple() {
        assertMethod("getInteger", "int",
                typeOf(ConcreteClass.class), "integer");
        assertMethod("getIntegerRef", "Integer",
                typeOf(ConcreteClass.class), "integerRef");
        assertMethod("strings", "void",
                typeOf(ConcreteClass.class), "strings", String.class, String.class);
    }

    @Test
    public void overrides() {
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, String.class), "foo", "String");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, Integer.class), "foo", "Object");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, int.class), "foo", "Object");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, Object.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, Object.class, Object.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, Object.class, int.class), "foo", "void");
        assertMethod(MethodLookuper.findInvoker(typeOf(OverrideClass.class), "foo", String.class, int.class, int.class), "foo", "void");
    }

    @Test
    public void genericMethods() {
        assertNoMethod("Type variable T is unresolvable", typeOf(GenericMethods.class), "unresolvable", int.class);
        assertMethod(MethodLookuper.findInvoker(typeOf(GenericMethods.class), "foo", int.class), "foo", "String");
        assertMethod("foo2", "int", typeOf(GenericMethods.class), "foo2", int.class);
        assertMethod("foo2", "wrime.reflect.model.I1", typeOf(GenericMethods.class), "foo2", I1.class);
    }

    @Test
    public void genericClass() {
        assertMethod("iterator", "Iterator<Integer>", typeOf(GenericClass.class, nameOf(Integer.class)), "iterator");
        assertMethod("iterator", "Iterator<wrime.reflect.model.Enum1>", typeOf(GenericClass.class, nameOf(Enum1.class)), "iterator");
        assertMethod("iterator", "Iterator<wrime.reflect.model.GenericClass<String>>", typeOf(GenericClass.class, nameOf(GenericClass.class, nameOf(String.class))), "iterator");

        assertMethod("genericIterator", "Iterator<Integer>", typeOf(GenericClass.class, nameOf(Enum1.class)), "genericIterator", Integer.class);
        assertMethod("genericIterator", "Iterator<wrime.reflect.model.Enum1>", typeOf(GenericClass.class, nameOf(Enum1.class)), "genericIterator", Enum1.class);
        assertMethod("genericIterator", "Iterator<wrime.reflect.model.Enum1>", typeOf(GenericClass.class, nameOf(GenericClass.class, nameOf(String.class))), "genericIterator", Enum1.class);
    }

    @Test
    public void genericMethodWithRestriction() {
        assertNoMethod(typeOf(GenericMethods.class), "varargI1", Integer.class);
        assertNoMethod(typeOf(GenericMethods.class), "varargI1", I1.class, I2.class);
        assertNoMethod(typeOf(GenericMethods.class), "varargI1", I1.class, I2Impl.class);

        assertMethod("varargI1", "void", typeOf(GenericMethods.class), "varargI1", I1.class);
        assertMethod("varargI1", "void", typeOf(GenericMethods.class), "varargI1", I1.class, I1Impl.class);

        assertMethod("varargI1I2", "wrime.reflect.model.I2", typeOf(GenericMethods.class), "varargI1I2");
    }

    @Test
    public void concreteMap() {
        assertMethod("values", "Collection<wrime.reflect.model.I1>", typeOf(Map.class, nameOf(String.class), nameOf(I1.class)), "values");
        assertMethod("entrySet", "Set<java.util.Map.Entry<String, wrime.reflect.model.I1>>", typeOf(Map.class, nameOf(String.class), nameOf(I1.class)), "entrySet");
    }
}

