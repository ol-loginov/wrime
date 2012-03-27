package wrime.util;

import java.util.Locale;

public class EscapeUtils {
    public static String escapeJavaString(String text) {
        StringBuilder builder = new StringBuilder();
        for (int len = 0; len < text.length(); ++len) {
            char ch = text.charAt(len);
            if (ch < 32) {
                switch (ch) {
                    case '\b':
                        builder.append('\\');
                        builder.append('b');
                        break;
                    case '\n':
                        builder.append('\\');
                        builder.append('n');
                        break;
                    case '\t':
                        builder.append('\\');
                        builder.append('t');
                        break;
                    case '\f':
                        builder.append('\\');
                        builder.append('f');
                        break;
                    case '\r':
                        builder.append('\\');
                        builder.append('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            builder.append("\\u00").append(hex(ch));
                        } else {
                            builder.append("\\u000").append(hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        builder.append('\'');
                        break;
                    case '"':
                        builder.append('\\');
                        builder.append('"');
                        break;
                    case '\\':
                        builder.append('\\');
                        builder.append('\\');
                        break;
                    default:
                        builder.append(ch);
                        break;
                }
            }
        }
        return builder.toString();
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }

}