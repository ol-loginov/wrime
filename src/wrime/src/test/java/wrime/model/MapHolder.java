package wrime.model;

import java.util.TreeMap;

public class MapHolder<T> {
    private MapInstance<T> map; //= new MapInstance();

    public MapInstance getMap() {
        return map;
    }

    public <R> R values(R i) {
        return null;
    }

    public static abstract class MapInstance<T> extends TreeMap<T, T> {
    }
}
