package wrime.model;

import java.util.ArrayList;
import java.util.Iterator;

public class ForIterator2 {
    public Bean2Iterable getIterable() {
        return new Bean2Iterable();
    }

    private static class Bean2Iterable implements Iterable<Bean2> {
        @Override
        public Iterator<Bean2> iterator() {
            return new ArrayList<Bean2>().iterator();
        }
    }
}
