package wrime.model;

@SuppressWarnings("UnusedDeclaration")
public class MapHolder<T> {
    private MapInstance<T> map = new MapInstance<T>();

    public MapInstance<T> getMap() {
        return map;
    }

    public <R> R values(R i) {
        return null;
    }
}
