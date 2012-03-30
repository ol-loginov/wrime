package wrime.ast;

import java.util.List;

public class NamePath {
    private final List<Name> path;

    public NamePath(List<Name> path) {
        this.path = path;
    }

    public List<Name> getPath() {
        return path;
    }
}
