package wrime.functor;

@SuppressWarnings("UnusedDeclaration")
public class StringFunctor {
    public String repeat(String ch, int count) {
        StringBuilder str = new StringBuilder();
        while (count-- > 0) {
            str.append(ch);
        }
        return str.toString();
    }

    public String repeat(char ch, int count) {
        StringBuilder str = new StringBuilder();
        while (count-- > 0) {
            str.append(ch);
        }
        return str.toString();
    }

    public String concat(Object... values) {
        StringBuilder result = new StringBuilder();
        for (Object v : values) {
            if (v != null) {
                result.append(v);
            }
        }
        return result.toString();
    }

    public boolean ne(String text) {
        return !e(text);
    }

    public boolean e(String text) {
        return text == null || text.length() == 0;
    }
}
