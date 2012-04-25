package wrime.model;

import java.util.Collection;

public class MapHolder {
    private MapInstance map; //= new MapInstance();

    public MapInstance getMap() {
        return map;
    }

    public static abstract class MapInstance implements Collection<String> {
    }
}
