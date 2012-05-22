package wrime.bytecode;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import wrime.*;
import wrime.antlr.EmitterFactory;
import wrime.antlr.WrimeExpressionLexer;
import wrime.antlr.WrimeExpressionParser;
import wrime.ast.Emitter;
import wrime.ast.EmitterWriter;
import wrime.ast.WrimeTag;
import wrime.output.BodyWriter;
import wrime.output.WrimeWriter;
import wrime.reflect.Types;
import wrime.scanner.WrimeScanner;
import wrime.tags.CallMatcher;
import wrime.tags.TagFactory;
import wrime.tags.TagProcessor;
import wrime.util.DefaultUtil;
import wrime.util.EscapeUtils;
import wrime.util.FunctorField;
import wrime.util.ParameterField;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SourceComposer implements SourceExpressionListener {
    private static final String EOL = System.getProperty("line.separator");
    private static final String SCOPE_IDENT = "  ";

    private static final String ESCAPED_WRITE_METHOD = "this.$$e";
    private static final String RAW_WRITE_METHOD = "this.$$r";
    private static final String TEXT_WRITE_METHOD = "this.$$t";

    private Body renderContentBody;

    private SourceExpression sourceExpression;

    private boolean classDone;
    private String className;

    private String functorPrefix;
    private Map<String, FunctorField> functorNames = new HashMap<String, FunctorField>();

    private Map<String, TagFactory> tagFactories = new TreeMap<String, TagFactory>();

    public SourceComposer(ClassLoader classLoader, Map<String, FunctorClass> functors, Map<String, TagFactory> customTags) throws WrimeException {
        renderContentBody = new Body();
        sourceExpression = new SourceExpression(classLoader, functorNames, this);

        for (Map.Entry<String, FunctorClass> kv : functors.entrySet()) {
            functorNames.put(kv.getKey(), new FunctorField(kv.getKey(), kv.getValue().getFunctorType(), toFieldIdentifier(kv.getKey())));
        }

        tagFactories.putAll(customTags);
    }

    public void configure(Map<WrimeEngine.Compiler, String> options) {
        functorPrefix = options.get(WrimeEngine.Compiler.FUNCTOR_PREFIX);
    }

    public static void throwError(String name) throws WrimeException {
        throwError(name, null);
    }

    public static void throwError(String name, Throwable cause) throws WrimeException {
        throw new WrimeException("Compiler exception: " + name, cause);
    }

    public static boolean isIdentifier(String name) {
        boolean firstPassed = false;
        boolean valid = true;
        for (char ch : name.toCharArray()) {
            if (!firstPassed) {
                valid = Character.isJavaIdentifierStart(ch);
                firstPassed = true;
            } else {
                valid &= Character.isJavaIdentifierPart(ch);
            }
        }
        return firstPassed && valid;
    }

    public static String toFieldIdentifier(String name) throws WrimeException {
        StringBuilder builder = new StringBuilder();
        builder.append("$");
        for (char ch : name.toCharArray()) {
            if (Character.isJavaIdentifierPart(ch)) {
                builder.append(ch);
            } else if ('/' == ch || '\'' == ch) {
                builder.append("$");
            } else if ('.' == ch) {
                builder.append("_");
            } else {
                builder.append((int) ch);
            }
        }
        return builder.toString();
    }

    private static String toClassIdentifier(String name) throws WrimeException {
        return "W" + toFieldIdentifier(name);
    }

    public String getClassName() {
        return className;
    }

    public String getClassCode() throws IOException {
        Body body = new Body();
        for (String name : sourceExpression.getImports()) {
            body.line(String.format("import %s;", name));
        }

        body.nl().line(String.format("public class %s extends %s {", className, WrimeWriter.class.getName()))
                .scope()

                .a(new ModelParameterListDeclarator())
                .a(new ModelFunctorListDeclarator())
                .a(new DeclarationDelimiter())

                .line(String.format("public %s(Writer writer) {", className))
                .scope().line("super(writer);").leave()
                .line("}").nl()

                .line(String.format("protected void clear() {"))
                .scope().a(new ModelParameterListCleaner()).a(new ModelFunctorListCleaner()).line("super.clear();").leave()
                .line("}").nl()

                .line(String.format("protected void assignFields(Map<String, Object> model) {"))
                .scope().line("super.assignFields(model);").a(new ModelParameterListInitializer()).a(new ModelFunctorListInitializer()).leave()
                .line("}").nl()

                .line(String.format("protected void renderContent() throws Exception {"))
                .scope().a(renderContentBody).leave()
                .line("}")

                .leave()
                .a("}");
        return body.toString();
    }

    private void ensureNotReady() throws WrimeException {
        if (classDone) {
            throwError("class is ready");
        }
    }

    public WrimeScanner.Receiver createReceiver() {
        return new ScannerReceiver();
    }

    @Override
    public void scopeAdded() {
        renderContentBody = renderContentBody.scope();
    }

    @Override
    public void scopeRemoved() {
        renderContentBody = renderContentBody.leave();
    }

    private abstract static class BodyWriterForward implements BodyWriter {
        public abstract BodyWriter getWriter();

        @Override
        public BodyWriter append(Emitter emitter) throws IOException {
            return getWriter().append(emitter);
        }

        @Override
        public BodyWriter append(CharSequence string) throws IOException {
            return getWriter().append(string);
        }

        @Override
        public BodyWriter nl() throws IOException {
            return getWriter().nl();
        }

        @Override
        public BodyWriter line(CharSequence string) throws IOException {
            return getWriter().line(string);
        }
    }

    private static class Body implements BodyWriter {
        private final StringBuilder body;
        private final String prefix;
        private boolean addPrefix;

        public Body() {
            this(new StringBuilder(), "");
        }

        public Body(StringBuilder body, String prefix) {
            this.body = body;
            this.prefix = prefix;
            this.addPrefix = true;
        }

        @Override
        public Body nl() {
            body.append(EOL);
            addPrefix = true;
            return this;
        }

        @Override
        public Body append(CharSequence string) throws IOException {
            if (addPrefix) {
                body.append(prefix);
                addPrefix = false;
            }
            body.append(string);
            return this;
        }

        @Override
        public Body append(Emitter emitter) throws IOException {
            StringWriter emitterBody = new StringWriter();
            new EmitterWriter(emitterBody).write(emitter);
            return append(emitterBody.toString());
        }

        @Override
        public Body line(CharSequence string) throws IOException {
            return append(string).nl();
        }

        public Body a(String text) throws IOException {
            return append(text);
        }

        public Body a(Body other) throws IOException {
            return everyLine(other.toString());
        }

        public Body everyLine(String bunchOfLines) throws IOException {
            if (bunchOfLines.length() > 0) {
                Scanner scanner = new Scanner(new StringReader(bunchOfLines));
                scanner.useDelimiter(Pattern.compile(Pattern.quote(EOL)));
                while (scanner.hasNext()) {
                    line(scanner.next());
                }
            }
            return this;
        }

        public String toString() {
            return body.toString();
        }

        public Body scope() {
            return new Body(body, prefix + SCOPE_IDENT);
        }

        public Body leave() {
            return new Body(body, prefix.substring(SCOPE_IDENT.length()));
        }

        public Body a(BodyCallback callback) throws IOException {
            callback.in(this);
            return this;
        }
    }

    private interface BodyCallback {
        void in(BodyWriter body) throws IOException;
    }

    private class DeclarationDelimiter implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            if (!sourceExpression.getParameters().isEmpty() || !functorNames.isEmpty()) {
                body.nl();
            }
        }
    }

    private class ModelParameterListDeclarator implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (ParameterField parameter : sourceExpression.getParameters().values()) {
                String className = Types.getJavaSourceName(parameter.getType());
                body.line(String.format("private %s %s;", className, parameter.getName()));
            }
        }
    }

    private class ModelParameterListCleaner implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (ParameterField parameter : sourceExpression.getParameters().values()) {
                body.line(String.format("this.%s=%s;", parameter.getName(), DefaultUtil.getDefault(parameter.getType())));
            }
        }
    }

    private class ModelParameterListInitializer implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (ParameterField parameter : sourceExpression.getParameters().values()) {
                body.line(String.format("this.%s=(%s)model.get(\"%s\");",
                        parameter.getName(),
                        Types.getJavaSourceName(parameter.getType()),
                        EscapeUtils.escapeJavaString(parameter.getName())));
            }
        }
    }

    private class ModelFunctorListDeclarator implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (FunctorField functor : functorNames.values()) {
                body.line(String.format("private %s %s;", Types.getJavaSourceName(functor.getType()), functor.getField()));
            }
        }
    }

    private class ModelFunctorListCleaner implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (FunctorField functor : functorNames.values()) {
                body.line(String.format("this.%s=null;", functor.getField()));
            }
        }
    }

    private class ModelFunctorListInitializer implements BodyCallback {
        @Override
        public void in(BodyWriter body) throws IOException {
            for (FunctorField functor : functorNames.values()) {
                String functorKey = functorPrefix + functor.getName();
                body.line(String.format("this.%s=(%s)model.get(\"%s\");", functor.getField(), Types.getJavaSourceName(functor.getType()), functorKey));
            }
        }
    }

    private class ScannerReceiver implements WrimeScanner.Receiver {
        private String path;
        private int line;
        private int column;

        @Override
        public void setLocation(String path, int line, int column) {
            this.path = path;
            this.line = line;
            this.column = column;
        }

        @Override
        public void command(String command) throws WrimeException {
            boolean rawRender = false;
            if (command.startsWith("#")) {
                command = command.substring(1);
                rawRender = true;
            }

            WrimeExpressionParser.RecognitionErrorListener recognitionErrorListener = new WrimeExpressionParser.RecognitionErrorListener() {
                @Override
                public void report(RecognitionException e, String message) {
                    String innerMessage = "";
                    if (message == null || message.length() == 0) {
                        innerMessage = " (" + message + ")";
                    }
                    throw new WrimeException("lexical error in command" + innerMessage, e, path, line + e.line, column + e.charPositionInLine);
                }
            };

            WrimeExpressionLexer lexer = new WrimeExpressionLexer(new ANTLRStringStream(command));
            WrimeExpressionParser parser = new WrimeExpressionParser(new CommonTokenStream(lexer));
            parser.setRecognitionErrorListener(recognitionErrorListener);
            parser.setEmitterFactory(new EmitterFactory(new Location(path, line, column)));

            WrimeExpressionParser.command_return cmd;
            try {
                cmd = parser.command();
            } catch (RecognitionException e) {
                recognitionErrorListener.report(e, "");
                cmd = null;
            }

            try {
                renderCommand(rawRender, cmd);
            } catch (IOException e) {
                throw new WrimeException("error printing render body", e, path, line + 1, column + 1);
            }
        }

        private void renderCommand(boolean rawRender, WrimeExpressionParser.command_return cmd) throws IOException {
            if (cmd != null && cmd.expression != null) {
                commandExpression(cmd.expression, rawRender);
                return;
            }

            if (cmd != null && cmd.tag != null) {
                commandTag(cmd.tag).render(sourceExpression, new BodyWriterForward() {
                    @Override
                    public BodyWriter getWriter() {
                        return renderContentBody;
                    }
                });
                return;
            }

            throw new WrimeException("empty expression", null, path, line + 1, column + 1);
        }

        private void commandExpression(Emitter expression, boolean rawOutput) throws IOException {
            new CallMatcher(expression).matchTypes(sourceExpression);

            StringWriter writer = new StringWriter();
            boolean renderReturnValue = Types.isWritable(expression.getReturnType());

            new EmitterWriter(writer).write(expression);

            if (renderReturnValue) {
                renderContentBody
                        .append(rawOutput ? RAW_WRITE_METHOD : ESCAPED_WRITE_METHOD).append("(")
                        .append(writer.toString())
                        .line(");");
            } else {
                renderContentBody
                        .append(writer.toString())
                        .line(";");
            }
        }

        private TagProcessor commandTag(WrimeTag tag) {
            String processorKey = (tag.isCustomTag() ? "$" : "") + tag.getProcessor();
            TagFactory factory = tagFactories.get(processorKey);
            if (factory == null) {
                throw new WrimeException("No tag factory for name '" + processorKey + "'", null, tag.getLocation());
            }

            TagProcessor processor = factory.createProcessor(tag);
            if (processor == null) {
                throw new WrimeException("No tag processor for name '" + processorKey + "'", null, tag.getLocation());
            }
            return processor;
        }

        @Override
        public void startResource(ScriptResource resource) throws WrimeException {
            ensureNotReady();
            sourceExpression.addImport(java.io.Writer.class.getName());
            sourceExpression.addImport("java.lang.*");
            sourceExpression.addImport("java.util.*");
            className = toClassIdentifier(resource.getPath());
        }

        @Override
        public void finishResource() throws WrimeException {
            ensureNotReady();
            classDone = true;
        }

        @Override
        public void text(String text) throws WrimeException {
            ensureNotReady();
            if (text == null || text.length() <= 0) {
                return;
            }
            try {
                renderContentBody.line(String.format("%s(\"%s\");", TEXT_WRITE_METHOD, EscapeUtils.escapeJavaString(text)));
            } catch (IOException e) {
                throw new WrimeException("i/o error", e);
            }
        }
    }
}
