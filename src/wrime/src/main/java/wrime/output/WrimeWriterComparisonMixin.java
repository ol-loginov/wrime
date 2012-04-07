package wrime.output;

public abstract class WrimeWriterComparisonMixin {
    private boolean bothNullsOrEqual(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    protected boolean $c$Equal(String a, String b) {
        return bothNullsOrEqual(a, b);
    }

    protected boolean $c$Equal(String a, Character b) {
        return bothNullsOrEqual(a, b);
    }

    protected boolean $c$Equal(Character a, String b) {
        return bothNullsOrEqual(a, b);
    }

    protected boolean $c$Equal(Object a, Object b) {
        return bothNullsOrEqual(a, b);
    }
}
