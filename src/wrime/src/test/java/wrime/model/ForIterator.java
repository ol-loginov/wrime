package wrime.model;

import java.util.ArrayList;

@SuppressWarnings("UnusedDeclaration")
public class ForIterator {
    @SuppressWarnings("unchecked")
    public Iterable getObjectList() {
        return new ArrayList() {{
            add(new Bean1());
            add(new Bean2());
        }};
    }

    public ArrayList<Bean2> getBeanList() {
        return new ArrayList<Bean2>() {{
            add(new Bean2());
            add(new Bean2());
        }};
    }

    public Iterable<? super Bean2> getSuperBeanList() {
        return new ArrayList<Bean2>() {{
            add(new Bean2());
            add(new Bean2());
        }};
    }

    public Iterable<? extends Bean2> getExtendBeanList() {
        return new ArrayList<Bean2>() {{
            add(new Bean2());
            add(new Bean2());
        }};
    }

    public Iterable<? super Face1> getSuperFaceList() {
        return new ArrayList<Object>() {{
            add(new Bean1());
            add(new Bean2());
        }};
    }

    public Iterable<? extends Face1> getExtendFaceList() {
        return new ArrayList<Bean2>() {{
            add(new Bean2());
            add(new Bean2());
        }};
    }
}
