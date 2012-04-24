package wrime.functor;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ObjectFunctor {
    public boolean nn(Object obj) {
        return obj != null;
    }

    public boolean n(Object obj) {
        return obj == null;
    }

    public boolean empty(Collection c) {
        return c == null || c.isEmpty();
    }
}
