package wrime.functor;

public class ObjectFunctor {
    public boolean nn(Object obj) {
        return obj != null;
    }

    public boolean n(Object obj) {
        return obj == null;
    }
}
