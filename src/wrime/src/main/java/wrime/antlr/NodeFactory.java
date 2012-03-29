package wrime.antlr;

import org.antlr.runtime.Token;

public class NodeFactory implements WrimeExpressionParser.NodeFactory {
    @Override
    public Operand createLogical(Token act, Operand lhs, Operand rhs) {
        switch (act.getType()) {
            case WrimeExpressionParser.NOT:
                break;
            case WrimeExpressionParser.AND:
                break;
            case WrimeExpressionParser.XOR:
                break;
        }
        return null;
    }
}
