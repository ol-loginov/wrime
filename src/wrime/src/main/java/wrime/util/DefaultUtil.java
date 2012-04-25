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
        if (type.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (type.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (type.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (type.equals(int.class)) {
            return DEFAULT_INT;
        } else if (type.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (type.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (type.equals(double.class)) {
            return DEFAULT_DOUBLE;
        } else {
            return "null";
        }
    }
}
