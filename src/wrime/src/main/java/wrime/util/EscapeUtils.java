package wrime.util;

import java.util.Locale;
import java.util.Set;

public class EscapeUtils {
    public static final String EMPTY = "";

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

    public static <T> T defaultIfNull(T a, T b) {
        return a == null ? b : a;
    }

    public static String join(Set<String> classPath, String separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : classPath) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            builder.append(s);
        }
        return builder.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
}