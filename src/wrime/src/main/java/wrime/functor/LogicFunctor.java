package wrime.functor;

public class LogicFunctor {
    public boolean eq(Object lhs, Object rhs) {
        return lhs.equals(rhs);
    }

    public boolean and(boolean lhs, boolean rhs) {
        return rhs && lhs;
    }

    public boolean or(boolean lhs, boolean rhs) {
        return rhs || lhs;
    }

    public boolean xor(boolean lhs, boolean rhs) {
        return rhs ^ lhs;
    }

    public boolean not(boolean lhs) {
        return !lhs;
    }
}
