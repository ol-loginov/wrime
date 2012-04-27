package wrime.util;

import java.lang.reflect.Type;

@SuppressWarnings("UnusedDeclaration")
public class DefaultUtil {
    // These gets initialized to their default values
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;

    public static Object getDefault(Type type) {
        if (boolean.class.equals(type)) {
            return DEFAULT_BOOLEAN;
        } else if (byte.class.equals(type)) {
            return DEFAULT_BYTE;
        } else if (short.class.equals(type)) {
            return DEFAULT_SHORT;
        } else if (int.class.equals(type)) {
            return DEFAULT_INT;
        } else if (long.class.equals(type)) {
            return DEFAULT_LONG;
        } else if (float.class.equals(type)) {
            return DEFAULT_FLOAT;
        } else if (double.class.equals(type)) {
            return DEFAULT_DOUBLE;
        } else {
            return "null";
        }
    }
}
