package wrime.scanner;

import org.junit.Test;
import wrime.WrimeEngine;

import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class WrimeScannerImplTest {
    @Test
    public void eatSpace() {
        WrimeScannerImpl impl = new WrimeScannerImpl();
        impl.configure(new TreeMap<WrimeEngine.Scanner, String>() {{
            put(WrimeEngine.Scanner.EAT_SPACE, "");
        }});

        assertEquals("asdasd", impl.eatSpace("asdasd"));
        assertEquals("asdasd", impl.eatSpace("asdasd\n"));
    }
}
