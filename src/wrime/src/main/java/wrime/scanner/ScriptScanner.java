package wrime.scanner;

import wrime.WrimeException;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptScanner {
    private final static int BUFFER_SIZE = 1024;
    private final static Pattern EOL_PATTERN = Pattern.compile("\r\n|\r|\n");

    private final Reader reader;
    private boolean readClosed;
    private boolean readMore;

    private CharBuffer buffer;
    private int bufferFill;

    private Matcher matcher;
    private boolean lookingAt;

    private int column;
    private int line;

    public ScriptScanner(Reader reader) {
        this.readMore = false;
        this.readClosed = false;
        this.reader = reader;
        this.buffer = CharBuffer.allocate(BUFFER_SIZE);
        this.buffer.limit(0);
        this.bufferFill = 0;
        this.matcher = Pattern.compile("").matcher(this.buffer);
    }

    public String waitDelimiter(Pattern pattern) throws WrimeException {
        String value = waitDelimiter0(pattern);
        if (value != null) {
            calculatePosition(value);
        }
        return value;
    }

    private void calculatePosition(String value) {
        Matcher matcher = EOL_PATTERN.matcher(value);
        int end = 0;
        while (matcher.find()) {
            line++;
            column = 0;
            end = matcher.end();
        }
        column += value.length() - end;
    }

    public String waitDelimiter0(Pattern pattern) throws WrimeException {
        lookingAt = false;

        if (readClosed) {
            return null;
        }

        while (true) {
            String result = waitPattern(pattern);
            if (result != null) {
                return result;
            }
            if (readClosed) {
                String value = removeHead(bufferFill);
                return value == null || value.length() == 0 ? null : value;
            }
            if (readMore) {
                readInput();
            }
        }
    }

    public boolean lookingAt() {
        return lookingAt;
    }

    private String waitPattern(Pattern pattern) {
        buffer.position(0);

        matcher.usePattern(pattern);
        matcher.region(0, buffer.limit());

        boolean hasMatch = matcher.find();
        if (!hasMatch || matcher.requireEnd()) {
            readMore = true;
            return null;
        }

        lookingAt = matcher.start() == 0;
        if (lookingAt) {
            String value = matcher.group();
            removeHead(matcher.end());
            return value;
        } else {
            return removeHead(matcher.start());
        }
    }

    private void readInput() throws WrimeException {
        buffer.position(bufferFill);

        if (buffer.limit() == buffer.capacity()) {
            addBufferSpace();
        }

        // Prepare to receive data
        int p = buffer.position();
        buffer.position(buffer.limit());
        buffer.limit(buffer.capacity());

        int n;
        try {
            n = reader.read(buffer);
        } catch (IOException e) {
            throw new WrimeException("Unexpected EOF", e);
        }

        if (n == -1) {
            readClosed = true;
            readMore = false;
        }

        if (n > 0) {
            readMore = false;
        }

        // Restore current position and limit for reading
        bufferFill = buffer.position();
        buffer.limit(buffer.position());
        buffer.position(p);
    }

    private String removeHead(int position) {
        int limit = buffer.limit();
        String value = buffer.subSequence(0, position).toString();
        buffer.position(position);
        buffer.compact();
        buffer.position(0);
        buffer.limit(limit - position);
        bufferFill -= position;
        return value;
    }

    private void addBufferSpace() {
        buffer.position(0);

        // Gain space by growing buffer
        int newSize = (int) (buffer.capacity() * 1.5);
        CharBuffer newBuffer = CharBuffer.allocate(newSize);
        newBuffer.put(buffer);
        newBuffer.flip();
        buffer = newBuffer;

        matcher.reset(buffer);
    }

    public void skip(Pattern pattern) throws WrimeException {
        waitDelimiter(pattern);
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }
}
