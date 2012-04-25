package wrime.model;

import java.util.TreeMap;

public class MapHolder {
    private final MapInstance map = new MapInstance();

    public MapInstance getMap() {
        return map;
    }

    public static class MapInstance extends TreeMap<Integer, String> {
    }
}
