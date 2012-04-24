package wrime.output;

public class WrimeWriterComparisonMixin {
    private static boolean bothNullsOrEqual(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    public boolean Equal(String a, String b) {
        return bothNullsOrEqual(a, b);
    }

    public boolean Equal(String a, Character b) {
        return bothNullsOrEqual(a, b);
    }

    public boolean Equal(Character a, String b) {
        return bothNullsOrEqual(a, b);
    }

    public boolean Equal(Object a, Object b) {
        return bothNullsOrEqual(a, b);
    }
}
