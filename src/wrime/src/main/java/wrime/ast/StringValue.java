package wrime.ast;

import wrime.lang.TypeName;

public class StringValue extends Emitter {
    private final String value;

    public StringValue(String value) {
        if (isQuoted(value, "'")) {
            this.value = removeQuotes(value, "'");
        } else if (isQuoted(value, "\"")) {
            this.value = removeQuotes(value, "\"");
        } else {
            this.value = value;
        }
        setReturnType(new TypeName(String.class));
    }

    private static boolean isQuoted(String value, String quote) {
        return value.length() >= 2 * quote.length() && value.startsWith(quote) && value.endsWith(quote);
    }

    private static String removeQuotes(String value, String quote) {
        return value.substring(quote.length(), value.length() - quote.length());
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
