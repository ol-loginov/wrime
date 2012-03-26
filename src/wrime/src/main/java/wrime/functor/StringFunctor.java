package wrime.functor;

public class StringFunctor {
    public String repeat(String ch, int count) {
        StringBuilder str = new StringBuilder();
        while (count-- > 0) {
            str.append(ch);
        }
        return str.toString();
    }
}
