package wrime.util;

public class EscapeUtils {
    public static String escapeJavaString(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
