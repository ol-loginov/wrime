package wrime.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class NodeFactoryTest {
    @Test
    public void tt() throws RecognitionException {
        WrimeExpressionLexer lexer = new WrimeExpressionLexer(new ANTLRStringStream("true and true and true"));
        WrimeExpressionParser parser = new WrimeExpressionParser(new CommonTokenStream(lexer));
        parser.setNodeFactory(new NodeFactory());
        WrimeExpressionParser.command_return cmd = parser.command();
    }
}

