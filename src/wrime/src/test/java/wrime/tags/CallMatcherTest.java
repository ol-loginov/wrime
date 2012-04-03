package wrime.tags;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import wrime.ExpressionScopeMock;
import wrime.Location;
import wrime.antlr.EmitterFactory;
import wrime.antlr.WrimeExpressionLexer;
import wrime.antlr.WrimeExpressionParser;
import wrime.ast.Emitter;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CallMatcherTest {
    private Emitter getExpression(String expression) throws RecognitionException, IOException {
        WrimeExpressionLexer lexer = new WrimeExpressionLexer(new ANTLRStringStream(expression));
        WrimeExpressionParser parser = new WrimeExpressionParser(new CommonTokenStream(lexer));
        parser.setEmitterFactory(new EmitterFactory(new Location()));
        WrimeExpressionParser.command_return cmd = parser.command();
        assertNotNull(cmd.expression);
        return cmd.expression;
    }

    private void match(String expression, Class returnType) throws IOException, RecognitionException {
        ExpressionScopeMock scope = createScope();
        Emitter emitter = getExpression(expression);
        CallMatcher matcher = new CallMatcher(emitter);
        matcher.matchTypes(scope);
        assertEquals(returnType, emitter.getReturnType().getType());
    }

    private ExpressionScopeMock createScope() {
        return new ExpressionScopeMock();
    }

    @Test
    public void солянка() throws RecognitionException, IOException {
        match(" true ", boolean.class);
        match(" false ", boolean.class);
        match("true and false", boolean.class);
        match("true or false", boolean.class);
        match("true xor false", boolean.class);
        match("not true or not false", boolean.class);

        match("1", int.class);
        match("1.1", float.class);
        match("" + (Float.MAX_VALUE + 1), double.class);

        match("1 lt -3 and 2 gt 3", boolean.class);
        match("1 - 3 + 3", int.class);
    }
}
