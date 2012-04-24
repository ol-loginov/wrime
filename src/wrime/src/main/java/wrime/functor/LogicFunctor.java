package wrime.functor;

@SuppressWarnings("UnusedDeclaration")
public class LogicFunctor {
    public boolean eq(Object lhs, Object rhs) {
        return lhs.equals(rhs);
    }

    public boolean gt(int a, int b) {
        return a > b;
    }

    public boolean lt(int a, int b) {
        return a < b;
    }

    public boolean lte(int a, int b) {
        return a <= b;
    }

    public boolean gte(int a, int b) {
        return a >= b;
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
