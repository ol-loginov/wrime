package wrime.bytecode;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import wrime.Location;
import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.WrimeException;
import wrime.antlr.EmitterFactory;
import wrime.antlr.WrimeExpressionLexer;
import wrime.antlr.WrimeExpressionParser;
import wrime.ast.Emitter;
import wrime.ast.WrimeTag;
import wrime.ops.EscapedRenderer;
import wrime.ops.Functor;
import wrime.ops.Operand;
import wrime.ops.OperandRendererDefault;
import wrime.output.WrimeWriter;
import wrime.scanner.WrimeScanner;
import wrime.tags.TagFactory;
import wrime.tags.TagProcessor;
import wrime.util.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

public class SourceComposer implements ExpressionRuntime {
    private static final String EOL = System.getProperty("line.separator");
    private static final String SCOPE_IDENT = "  ";

    private static final String ESCAPED_WRITE_METHOD = "this.$$e";
    private static final String RAW_WRITE_METHOD = "this.$$r";
    private static final String TEXT_WRITE_METHOD = "this.$$t";

    private Body renderContentBody;

    private ExpressionContextRoot expressionContext;

    private boolean classDone;
    private String className;

    private List<String> importNames = new ArrayList<String>();
    private Map<String, ParameterName> parameterNames = new HashMap<String, ParameterName>();

    private String functorPrefix;
    private Map<String, FunctorName> functorNames = new HashMap<String, FunctorName>();

    private Map<String, TagFactory> tagFactories = new TreeMap<String, TagFactory>();

    public SourceComposer(ClassLoader classLoader, Map<String, Object> functors, Map<String, TagFactory> customTags) throws WrimeException {
        renderContentBody = new Body();
        expressionContext = new ExpressionContextRoot(this, classLoader);

        for (Map.Entry<String, Object> kv : functors.entrySet()) {
            functorNames.put(kv.getKey(), new FunctorName(kv.getKey(), kv.getValue().getClass(), toFieldIdentifier(kv.getKey())));
        }

        tagFactories.putAll(customTags);
    }

    public void configure(Map<WrimeEngine.Compiler, String> options) {
        functorPrefix = options.get(WrimeEngine.Compiler.FUNCTOR_PREFIX);
    }

    private void error(String name) throws WrimeException {
        error(name, null);
    }

    private void error(String name, Throwable cause) throws WrimeException {
        throw new WrimeException("Compiler exception: " + name, cause);
    }

    private static boolean isIdentifier(String name) {
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

    private static String toFieldIdentifier(String name) throws WrimeException {
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

    public String getClassCode() {
        Body body = new Body();
        for (String name : importNames) {
            body.l(String.format("import %s;", name));
        }

        body.nl().l(String.format("public class %s extends %s {", className, WrimeWriter.class.getName()))
                .scope()

                .a(new ModelParameterListDeclarator())
                .a(new ModelFunctorListDeclarator())
                .a(new DeclarationDelimiter())

                .l(String.format("public %s(Writer writer) {", className))
                .scope().l("super(writer);").leave()
                .l("}").nl()

                .l(String.format("protected void clear() {"))
                .scope().a(new ModelParameterListCleaner()).a(new ModelFunctorListCleaner()).l("super.clear();").leave()
                .l("}").nl()

                .l(String.format("protected void assignFields(Map<String, Object> model) {"))
                .scope().l("super.assignFields(model);").a(new ModelParameterListInitializer()).a(new ModelFunctorListInitializer()).leave()
                .l("}").nl()

                .l(String.format("protected void renderContent() throws Exception {"))
                .scope().a(renderContentBody).leave()
                .l("}")

                .leave()
                .a("}");
        return body.toString();
    }

    private void ensureNotReady() throws WrimeException {
        if (classDone) {
            error("class is ready");
        }
    }

    @Override
    public void addImport(String clazz) {
        importNames.add(clazz);
    }

    @Override
    public Collection<String> getImports() {
        return importNames;
    }

    public WrimeScanner.Receiver createReceiver() {
        return new ScannerReceiver();
    }

    @Override
    public Collection<ParameterName> getModelParameters() {
        return parameterNames.values();
    }

    @Override
    public void addModelParameter(String parameterName, String parameterTypeDef, Class parameterClass, String option) throws WrimeException {
        if (!isIdentifier(parameterName)) {
            error("not a Java identifier " + parameterName);
        }
        if (parameterNames.containsKey(parameterName)) {
            error("duplicate for model parameter " + parameterName);
        }
        TypeName def = new TypeName(parameterClass, parameterTypeDef);
        expressionContext.addVar(parameterName, def);
        parameterNames.put(parameterName, new ParameterName(parameterName, def, option));
    }

    @Override
    public ParameterName getModelParameter(String name) {
        return parameterNames.get(name);
    }

    @Override
    public void scopeAdded() {
        renderContentBody = renderContentBody.scope();
    }

    @Override
    public void scopeRemoved() {
        renderContentBody = renderContentBody.leave();
    }

    @Override
    public TypeName findFunctorType(String name) {
        FunctorName functor = functorNames.get(name);
        return functor != null ? new TypeName(functor.getType()) : null;
    }

    private static class Body {
        private final StringBuilder body;
        private final String prefix;

        public Body() {
            this(new StringBuilder(), "");
        }

        public Body(StringBuilder body, String prefix) {
            this.body = body;
            this.prefix = prefix;
        }

        public Body a(String text) {
            body.append(prefix).append(text);
            return this;
        }

        public Body l(String text) {
            return a(text).nl();
        }

        public Body a(Body other) {
            return everyLine(other.toString());
        }

        public Body everyLine(String bunchOfLines) {
            if (bunchOfLines.length() > 0) {
                Scanner scanner = new Scanner(new StringReader(bunchOfLines));
                scanner.useDelimiter(Pattern.compile(Pattern.quote(EOL)));
                while (scanner.hasNext()) {
                    l(scanner.next());
                }
            }
            return this;
        }

        public Body nl() {
            body.append(EOL);
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

        public Body a(BodyCallback callback) {
            callback.in(this);
            return this;
        }
    }

    private interface BodyCallback {
        void in(Body body);
    }

    private class DeclarationDelimiter implements BodyCallback {
        @Override
        public void in(Body body) {
            if (getModelParameters().size() > 0 || functorNames.size() > 0) {
                body.nl();
            }
        }
    }

    private class ModelParameterListDeclarator implements BodyCallback {
        @Override
        public void in(Body body) {
            for (ParameterName parameter : getModelParameters()) {
                body.l(String.format("private %s %s;",
                        parameter.getType().getAlias(),
                        parameter.getName()));
            }
        }
    }

    private class ModelParameterListCleaner implements BodyCallback {
        @Override
        public void in(Body body) {
            for (ParameterName parameter : getModelParameters()) {
                body.l(String.format("this.%s=%s;",
                        parameter.getName(),
                        Defaults.getDefaultValueString(parameter.getType().getType())));
            }
        }
    }

    private class ModelParameterListInitializer implements BodyCallback {
        @Override
        public void in(Body body) {
            for (ParameterName parameter : getModelParameters()) {
                body.l(String.format("this.%s=(%s)model.get(\"%s\");",
                        parameter.getName(),
                        parameter.getType().getAlias(),
                        EscapeUtils.escapeJavaString(parameter.getName())));
            }
        }
    }

    private class ModelFunctorListDeclarator implements BodyCallback {
        @Override
        public void in(Body body) {
            for (FunctorName functor : functorNames.values()) {
                body.l(String.format("private %s %s;",
                        TypeWrap.create(functor.getType()).getJavaSourceName(),
                        functor.getField()));
            }
        }
    }

    private class ModelFunctorListCleaner implements BodyCallback {
        @Override
        public void in(Body body) {
            for (FunctorName functor : functorNames.values()) {
                body.l(String.format("this.%s=null;", functor.getField()));
            }
        }
    }

    private class ModelFunctorListInitializer implements BodyCallback {
        @Override
        public void in(Body body) {
            for (FunctorName functor : functorNames.values()) {
                String functorKey = functorPrefix + functor.getName();
                body.l(String.format("this.%s=(%s)model.get(\"%s\");", functor.getField(), TypeWrap.create(functor.getType()).getJavaSourceName(), functorKey));
            }
        }
    }

    public static class Defaults {
        // These gets initialized to their default values
        private static boolean DEFAULT_BOOLEAN;
        private static byte DEFAULT_BYTE;
        private static short DEFAULT_SHORT;
        private static int DEFAULT_INT;
        private static long DEFAULT_LONG;
        private static float DEFAULT_FLOAT;
        private static double DEFAULT_DOUBLE;

        public static Object getDefaultValueString(Type type) {
            if (type.equals(boolean.class)) {
                return DEFAULT_BOOLEAN;
            } else if (type.equals(byte.class)) {
                return DEFAULT_BYTE;
            } else if (type.equals(short.class)) {
                return DEFAULT_SHORT;
            } else if (type.equals(int.class)) {
                return DEFAULT_INT;
            } else if (type.equals(long.class)) {
                return DEFAULT_LONG;
            } else if (type.equals(float.class)) {
                return DEFAULT_FLOAT;
            } else if (type.equals(double.class)) {
                return DEFAULT_DOUBLE;
            } else {
                return "null";
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
                TagProcessor processor = commandTag(cmd.tag);
                StringWriter content = new StringWriter();
                processor.render(expressionContext, content);
                renderContentBody.everyLine(content.toString());
                return;
            }

            throw new WrimeException("empty expression", null, path, line + 1, column + 1);
        }

        private void commandExpression(Emitter expression, boolean rawOutput) {
            /*
            DirectCallRenderer callRenderer = new DirectCallRenderer();
            RootReceiver rootReceiver = new RootReceiver(tagFactories, callRenderer);
            PathContext expressionPath = new PathContext(callRenderer, rootReceiver);
            */
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
            expressionContext.addImport(java.io.Writer.class);
            expressionContext.addImport("java.lang.*");
            expressionContext.addImport("java.util.*");
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
            if (text != null && text.length() > 0) {
                renderContentBody.l(String.format("%s(\"%s\");", TEXT_WRITE_METHOD, EscapeUtils.escapeJavaString(text)));
            }
        }
    }

    private class DirectCallRenderer extends OperandRendererDefault implements EscapedRenderer {
        private boolean escapeBeforeWrite;

        @Override
        public void escapeBeforeWrite(boolean enable) {
            this.escapeBeforeWrite = enable;
        }

        @Override
        public void render(Operand operand) throws WrimeException {
            if (operand == null) {
                return;
            }

            StringWriter writer = new StringWriter();
            try {
                super.render(operand, writer);
            } catch (IOException e) {
                error("writer error", e);
            }

            String closer = "";
            if (operand.isStatement()) {
                closer = ";";
            }

            String format;
            if (isWritable(operand.getResult())) {
                if (escapeBeforeWrite) {
                    format = ESCAPED_WRITE_METHOD + "(%s)%s";
                } else {
                    format = RAW_WRITE_METHOD + "(%s)%s";
                }
            } else {
                format = "%s%s";
            }
            renderContentBody.everyLine(String.format(format, writer.toString(), closer));
        }

        @Override
        public void render(Functor operand, Writer writer) throws IOException {
            FunctorName functor = functorNames.get(operand.getName());
            writer.append(String.format("this.%s", functor.getField()));
        }

        private boolean isWritable(TypeName def) {
            return def != null && def.getType() != null && !def.getType().equals(Void.TYPE);
        }
    }
}
