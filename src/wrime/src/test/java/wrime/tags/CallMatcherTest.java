package wrime.tags;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import wrime.ExpressionKeeperMock;
import wrime.Location;
import wrime.antlr.EmitterFactory;
import wrime.antlr.WrimeExpressionLexer;
import wrime.antlr.WrimeExpressionParser;
import wrime.ast.Emitter;
import wrime.functor.StringFunctor;
import wrime.lang.TypeInstance;
import wrime.model.Bean0;
import wrime.model.Bean1;
import wrime.model.Bean2;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        Emitter emitter = getExpression(expression);
        CallMatcher matcher = new CallMatcher(emitter);
        matcher.matchTypes(new ExpressionKeeperMock());
        assertTrue(emitter.getReturnType().isA(returnType));
    }

    private void match(String expression, Class returnType, ExpressionKeeperMock scope) throws IOException, RecognitionException {
        Emitter emitter = getExpression(expression);
        CallMatcher matcher = new CallMatcher(emitter);
        matcher.matchTypes(scope);
        assertNotNull(emitter.getReturnType());
        assertTrue(emitter.getReturnType().isA(returnType));
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

        match("' '", String.class);
    }

    @Test
    public void variables() throws RecognitionException, IOException {
        ExpressionKeeperMock keeper = new ExpressionKeeperMock();
        keeper.current().getVariables().put("self", new TypeInstance(CallMatcher.class));
        keeper.current().getVariables().put("bean0", new TypeInstance(Bean0.class));
        keeper.current().getVariables().put("bean1", new TypeInstance(Bean1.class));
        keeper.current().getVariables().put("bean2", new TypeInstance(Bean2.class));
        keeper.getFunctors().put("str", new TypeInstance(StringFunctor.class));

        match("bean2.callSelf(1).call(2)", Void.TYPE, keeper);
        match("self", CallMatcher.class, keeper);
        match("bean1.bean.arg1()", String.class, keeper);
        match("bean1.bean.hello", String.class, keeper);

        match("str:concat('12', '1', '3')", String.class, keeper);
        match("str:repeat('12', 3)", String.class, keeper);
        match("str:ne(null)", boolean.class, keeper);
        match("str:ne('1')", boolean.class, keeper);
    }
}
