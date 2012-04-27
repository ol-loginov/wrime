package wrime.model;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class MapHolder<T> {
    private MapInstance<T> map = new MapInstance<T>();

    public MapInstance<T> getMap() {
        return map;
    }

    public List<String> strings() {
        return null;
    }

    public <R> R values(R i) {
        return null;
    }
}
