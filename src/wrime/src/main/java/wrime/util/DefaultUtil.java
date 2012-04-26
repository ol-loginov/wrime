package wrime.util;

import wrime.lang.TypeDef;

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

    public static Object getDefault(TypeDef type) {
        if (type.isA(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (type.isA(byte.class)) {
            return DEFAULT_BYTE;
        } else if (type.isA(short.class)) {
            return DEFAULT_SHORT;
        } else if (type.isA(int.class)) {
            return DEFAULT_INT;
        } else if (type.isA(long.class)) {
            return DEFAULT_LONG;
        } else if (type.isA(float.class)) {
            return DEFAULT_FLOAT;
        } else if (type.isA(double.class)) {
            return DEFAULT_DOUBLE;
        } else {
            return "null";
        }
    }
}
