package wrime.reflect;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypesTest {
    @Test
    public void testBoolean() throws ClassNotFoundException {
        assertTrue(Types.isBoolean(boolean.class));
        assertTrue(Types.isBoolean(Boolean.class));
        assertFalse(Types.isBoolean(null));
        assertFalse(Types.isBoolean(Types.NULL_TYPE));
    }
}
