package wrime.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import wrime.Location;
import wrime.ast.EmitterWriter;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmitTest {
    private void checkExpression(String expression, String expected) throws RecognitionException, IOException {
        WrimeExpressionLexer lexer = new WrimeExpressionLexer(new ANTLRStringStream(expression));
        WrimeExpressionParser parser = new WrimeExpressionParser(new CommonTokenStream(lexer));
        parser.setEmitterFactory(new EmitterFactory(new Location()));
        WrimeExpressionParser.command_return cmd = parser.command();
        assertNotNull(cmd.expression);

        StringWriter writer = new StringWriter();
        new EmitterWriter(writer).write(cmd.expression);
        assertEquals(expected, writer.toString());
    }

    @Test
    public void солянка() throws RecognitionException, IOException {
        checkExpression("1 and 2 or 3", "1 && 2 || 3");
        checkExpression("0 xor (1 and 2) or 3", "0 ^ (1 && 2) || 3");
        checkExpression("1 and (2 or 3)", "1 && (2 || 3)");
        checkExpression("1 and not (2 or 3)", "1 && !(2 || 3)");

        checkExpression("1 gt (2 lt 3 gte 3)", "1 > (2 < 3 >= 3)");
        checkExpression("1 gt 2 lt 3 gte 3 lte 4 eq 5 neq 6", "1 > 2 < 3 >= 3 <= 4 == 5 != 6");

        checkExpression("1+2-3*4/5%4", "1 + 2 - 3 * 4 / 5 % 4");

        checkExpression("true   and    false        or      null", "true && false || null");
        checkExpression("'true'   and    \"false\"        or      \"nu'll\"", "\"true\" && \"false\" || \"nu'll\"");

        checkExpression("i18n:translate('asdasd', 100)", "this.$$i18n.translate(\"asdasd\", 100)");
        checkExpression("agone.bubba.value", "agone.bubba.value()");
    }
}

