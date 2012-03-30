package wrime.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import wrime.Location;

public class NodeFactoryTest {
    @Test
    public void tt() throws RecognitionException {
        WrimeExpressionLexer lexer = new WrimeExpressionLexer(new ANTLRStringStream("1 and 2 or 3"));
        WrimeExpressionParser parser = new WrimeExpressionParser(new CommonTokenStream(lexer));
        parser.setEmitterFactory(new EmitterFactory(new Location()));
        WrimeExpressionParser.command_return cmd = parser.command();
        Gate gate = (Gate) cmd.expression;
    }
}

