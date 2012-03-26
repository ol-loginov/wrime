package wrime.scanner;

import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.WrimeException;
import wrime.ops.EscapedRenderer;
import wrime.ops.Functor;
import wrime.ops.Operand;
import wrime.ops.OperandRendererDefault;
import wrime.output.WrimeWriter;
import wrime.tags.PathContext;
import wrime.tags.RootReceiver;
import wrime.tags.TagFactory;
import wrime.util.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

public class WrimeCompiler {
    private static final String EOL = System.getProperty("line.separator");
    private static final String SCOPE_IDENT = "  ";

    private static final String ESCAPED_WRITE_METHOD = "this.$$e";
    private static final String RAW_WRITE_METHOD = "this.$$r";
    private static final String TEXT_WRITE_METHOD = "this.$$t";

    private Body renderContentBody;

    private PathContext expressionPath;
    private ExpressionContextImpl expressionContext;

    private boolean classDone;
    private String className;

    private List<String> importNames = new ArrayList<String>();
    private Map<String, ParameterName> parameterNames = new HashMap<String, ParameterName>();

    private String functorPrefix;
    private Map<String, FunctorName> functorNames = new HashMap<String, FunctorName>();

    private List<TagFactory> tagFactories = new ArrayList<TagFactory>();

    public WrimeCompiler(WrimeEngine engine) throws WrimeException {
        renderContentBody = new Body();
        expressionContext = new ExpressionContextImpl(this, engine.getRootLoader());

        for (Map.Entry<String, Object> kv : engine.getFunctors()) {
            functorNames.put(kv.getKey(), new FunctorName(kv.getKey(), kv.getValue().getClass(), toFieldIdentifier(kv.getKey())));
        }

        tagFactories.addAll(engine.getTags());
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

    private void ensureInsideExpression(boolean shouldHaveExpression) throws WrimeException {
        if (expressionPath != null ^ shouldHaveExpression) {
            error("unexpected expression statement");
        }
    }

    private void completeExpression() throws WrimeException {
        expressionPath.markComplete(expressionContext);
        expressionPath = null;
    }

    private void startExpression() throws WrimeException {
        DirectCallRenderer callRenderer = new DirectCallRenderer();
        RootReceiver rootReceiver = new RootReceiver(tagFactories, callRenderer);
        expressionPath = new PathContext(callRenderer, rootReceiver);
    }

    public void addImport(String clazz) {
        importNames.add(clazz);
    }

    public Collection<String> getImports() {
        return importNames;
    }

    public WrimeScanner.Receiver createReceiver() {
        return new ScannerReceiver();
    }

    public Collection<ParameterName> getModelParameters() {
        return parameterNames.values();
    }

    public void addModelParameter(String parameterName, String parameterTypeDef, Class parameterClass, String option) throws WrimeException {
        if (!isIdentifier(parameterName)) {
            error("not a Java identifier " + parameterName);
        }
        if (parameterNames.containsKey(parameterName)) {
            error("duplicate for model parameter " + parameterName);
        }
        TypeName def = new TypeName();
        def.setType(parameterClass);
        def.setAlias(parameterTypeDef);
        expressionContext.addVar(parameterName, def);
        parameterNames.put(parameterName, new ParameterName(parameterName, def, option));
    }

    public ParameterName getModelParameter(String name) {
        return parameterNames.get(name);
    }

    public void scopeAdded() {
        renderContentBody = renderContentBody.scope();
    }

    public void scopeRemoved() {
        renderContentBody = renderContentBody.leave();
    }

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
            Scanner scanner = new Scanner(new StringReader(bunchOfLines));
            scanner.useDelimiter(Pattern.compile(Pattern.quote(EOL)));
            while (scanner.hasNext()) {
                l(scanner.next());
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
        @Override
        public void setLocation(String path, int line, int column) {
            if (expressionPath == null) {
                return;
            }
            expressionPath.setPosition(path, line, column);
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
            ensureInsideExpression(false);
            classDone = true;
        }

        @Override
        public void text(String text) throws WrimeException {
            ensureNotReady();
            if (text != null && text.length() > 0) {
                renderContentBody.l(String.format("%s(\"%s\");", TEXT_WRITE_METHOD, EscapeUtils.escapeJavaString(text)));
            }
        }

        @Override
        public void exprStart() throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(false);
            startExpression();
        }

        @Override
        public void exprFinish() throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            completeExpression();
        }

        @Override
        public void exprListOpen() throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            expressionPath.current().beginList(expressionContext);
        }

        @Override
        public void exprListClose() throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            expressionPath.current().closeList(expressionContext);
        }

        @Override
        public void exprName(String name) throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            expressionPath.current().pushToken(expressionContext, name);
        }

        @Override
        public void exprLiteral(String literal) throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            expressionPath.current().pushLiteral(expressionContext, literal);
        }

        @Override
        public void exprDelimiter(String value) throws WrimeException {
            ensureNotReady();
            ensureInsideExpression(true);
            expressionPath.current().pushDelimiter(expressionContext, value);
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