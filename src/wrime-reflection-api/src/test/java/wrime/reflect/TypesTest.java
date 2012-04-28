package wrime.reflect;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class TypesTest {
    @Test
    public void testBoolean() throws ClassNotFoundException {
        assertTrue(Types.isBoolean(boolean.class));
        assertTrue(Types.isBoolean(Boolean.class));
        assertFalse(Types.isBoolean(null));
        assertFalse(Types.isBoolean(Types.NULL_TYPE));
    }

    @Test
    public void getJavaSourceName() {
        assertEquals("java.util.Map", Types.getJavaSourceName(Map.class));
        assertEquals("Map", Types.getJavaSourceName(Map.class, Arrays.asList("java.util.Map")));
        assertEquals("Map", Types.getJavaSourceName(Map.class, Arrays.asList("java.util.*")));

        assertEquals("Map", Types.getJavaSourceName(Map.Entry.class, Arrays.asList("java.util.*")));
    }
}
