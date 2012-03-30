package wrime;

public class Location {
    private String path;
    private int line;
    private int column;

    public Location() {
        this("", 0, 0);
    }

    public Location(String path, int line, int column) {
        this.path = path;
        this.line = line;
        this.column = column;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Location move(int line, int column) {
        Location next = new Location();
        next.path = path;
        next.line = this.line + line;
        next.column = this.column + column;
        return next;
    }
}
