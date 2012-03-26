package wrime.functor;

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

    public String concat(String... strings) {
        StringBuilder result = new StringBuilder();
        for (String str : strings) {
            if (str != null) {
                result.append(str);
            }
        }
        return result.toString();
    }
}
