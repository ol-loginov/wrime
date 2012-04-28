package wrime.reflect;

import org.junit.Test;
import wrime.reflect.model.GenericClass;
import wrime.reflect.model.GenericClassDerivative;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static wrime.reflect.TestModel.nameOf;
import static wrime.reflect.TestModel.typeOf;

public class TypeUtilTest {
    private static void assertType(Type type, String expectedName) {
        assertEquals(expectedName, Types.getJavaSourceName(type));
    }

    @Test
    public void findSuperclass() {
        assertType(TypeUtil.getSuperclass(typeOf(GenericClass.class, nameOf(int.class))), "java.lang.Object");
        assertType(TypeUtil.getSuperclass(typeOf(GenericClassDerivative.class, nameOf(Integer.class))), "wrime.reflect.model.GenericClass<java.lang.Integer>");
    }
}
