package wrime;

public class WrimeException extends RuntimeException {
    private String path;
    private int line;
    private int column;

    public WrimeException(String message, Throwable e) {
        super(message, e);
    }

    public WrimeException(String message, Throwable e, String path, int line, int column) {
        super(message + createLocationInfo(path, line, column), e);
        this.path = path;
        this.line = line;
        this.column = column;
    }

    public String getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public static String createLocationInfo(String path, int line, int column) {
        if (path == null) {
            return "";
        }
        return String.format(" (%s:%d, column %d)", path, line, column);
    }
}
