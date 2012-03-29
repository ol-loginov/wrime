package wrime.scanner;

import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.WrimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WrimeScannerImpl implements WrimeScanner {
    private Map<WrimeEngine.Scanner, String> options = new HashMap<WrimeEngine.Scanner, String>();

    public void configure(Map<WrimeEngine.Scanner, String> options) {
        this.options.clear();
        this.options.putAll(options);
    }

    public void parse(ScriptResource resource, Receiver receiver) throws WrimeException {
        receiver.startResource(resource);

        InputStream in = null;
        try {
            in = resource.getInputStream();
            parse(receiver, new InputStreamReader(in, WrimeEngine.UTF_8), resource.getPath());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // silent one
                }
            }
        }
    }

    private void parse(Receiver receiver, Reader reader, String path) throws WrimeException {
        ScannerWrapper scanner = new ScannerWrapper(reader);
        Token token = new Token();
        while (scanner.next(token)) {
            try {
                receiver.setLocation(path, token.line, token.column);
                accept(receiver, token);
                if (token.type == TokenType.EOF) {
                    return;
                }
            } catch (WrimeException e) {
                throw new WrimeException(e.getMessage(), e, path, token.line + 1, token.column + 1);
            }
        }
    }

    private void accept(Receiver receiver, Token token) throws WrimeException {
        switch (token.type) {
            case EOF:
                receiver.finishResource();
                return;
            case TEXT:
                receiver.text(eatSpace(token.value));
                break;
            case EXPR_START:
                receiver.exprStart();
                break;
            case EXPR_END:
                receiver.exprFinish();
                break;
            case EXPR_PART:
                receiver.exprPart(token.value);
                break;
            default:
                throw new IllegalStateException("this situation is not supported");
        }
    }

    protected String eatSpace(String value) {
        if (value != null) {
            if (!options.containsKey(WrimeEngine.Scanner.EAT_SPACE)) {
                return value;
            }
            value = value.replaceAll("\\s+$", "");
        }
        return value;
    }

    private static class ScannerWrapper {
        private ScriptScanner scanner;
        private Expectation expectation;

        private String latestContent = "";

        private ScannerWrapper(Reader reader) {
            scanner = new ScriptScanner(reader);
            expect(Expectation.TOKEN_MARK);
        }

        private void expect(Expectation expectation) {
            this.expectation = expectation;
        }

        public boolean next(Token token) throws WrimeException {
            token.clear();
            while (token.type == TokenType.INCOMPLETE) {
                expect(consume(token));
                latestContent = token.value;
            }
            return token.type != TokenType.INCOMPLETE;
        }

        private Expectation consume(Token token) throws WrimeException {
            token.line = scanner.line();
            token.column = scanner.column();
            token.value = scanner.waitDelimiter(expectation.pattern());
            if (token.value == null) {
                token.type = TokenType.EOF;
                return Expectation.TOKEN_MARK;
            }

            switch (expectation) {
                case TOKEN_MARK:
                    if (scanner.lookingAt()) {
                        token.type = TokenType.EXPR_START;
                        return Expectation.EXPR_DELIMITER;
                    } else {
                        token.type = TokenType.TEXT;
                        return Expectation.TOKEN_MARK;
                    }
                case EXPR_QUOTE_BOUND:
                    token.type = TokenType.EXPR_PART;
                    if (scanner.lookingAt() && latestContentBackCount("\\") % 2 == 1) {
                        scanner.skip(expectation.pattern());
                    }
                    return Expectation.EXPR_DELIMITER;
                case EXPR_DQUOTE_BOUND:
                    token.type = TokenType.EXPR_PART;
                    if (scanner.lookingAt() && latestContentBackCount("\\") % 2 == 1) {
                        scanner.skip(expectation.pattern());
                    }
                    return Expectation.EXPR_DELIMITER;
                case EXPR_DELIMITER:
                    token.type = TokenType.EXPR_PART;
                    if ("'".equals(token.value)) {
                        return Expectation.EXPR_QUOTE_BOUND;
                    } else if ("\"".equals(token.value)) {
                        return Expectation.EXPR_DQUOTE_BOUND;
                    } else if ("}".equals(token.value)) {
                        token.type = TokenType.EXPR_END;
                        return Expectation.TOKEN_MARK;
                    } else {
                        return Expectation.EXPR_DELIMITER;
                    }
                default:
                    throw new IllegalStateException("this situation is not supported");
            }
        }

        private int latestContentBackCount(String s) {
            if (latestContent == null) {
                return 0;
            }
            String end = latestContent;
            int count = 0;
            while (end.endsWith(s)) {
                count++;
                end = latestContent.substring(0, latestContent.length() - s.length());
            }
            return count;
        }
    }

    private static enum Expectation {
        TOKEN_MARK("\\$\\{"),
        EXPR_DELIMITER("'|\\\"|}"),
        EXPR_QUOTE_BOUND("\\\'"),
        EXPR_DQUOTE_BOUND("\\\"");

        private final Pattern pattern;

        public Pattern pattern() {
            return pattern;
        }

        private Expectation(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }
    }

    private static enum TokenType {
        INCOMPLETE,
        EOF,

        TEXT,
        EXPR_START,
        EXPR_END,
        EXPR_PART
    }

    private static class Token {
        private TokenType type;
        private String value;

        private int line;
        private int column;

        public Token() {
            clear();
        }

        public void clear() {
            type = TokenType.INCOMPLETE;
            value = "";
            line = 0;
            column = 0;
        }
    }
}
