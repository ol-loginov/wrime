package wrime.reflect.model;

import java.util.Iterator;

@SuppressWarnings("UnusedDeclaration")
public class GenericClass<T> implements Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        return null;
    }


    public <R> Iterator<R> genericIterator(R value) {
        return null;
    }
}
