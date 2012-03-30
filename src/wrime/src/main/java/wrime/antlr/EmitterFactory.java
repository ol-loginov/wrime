package wrime.antlr;

import org.antlr.runtime.Token;
import wrime.Location;
import wrime.WrimeException;

public class EmitterFactory implements WrimeExpressionParser.EmitterFactory {
    private final Location location;

    public EmitterFactory(Location location) {
        this.location = location;
    }

    private <T extends Emitter> T locatable(T emitter, Location location) {
        emitter.setLocation(location);
        return emitter;
    }

    private <T extends Emitter> T locatable(T emitter, Token token) {
        return locatable(emitter, location.move(token.getLine(), token.getCharPositionInLine()));
    }

    @Override
    public Emitter getNumber(Token token) {
        return locatable(new NumberLiteral(token.getText()), token);
    }

    @Override
    public Emitter getBool(boolean value) {
        return null;
    }

    @Override
    public Emitter getNull() {
        return null;
    }

    @Override
    public Emitter getString(String text) {
        return null;
    }

    @Override
    public Gate makeGate(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.AND:
                return locatable(new Gate(l, GateRule.AND, r), ruleLocation);
            case WrimeExpressionParser.OR:
                return locatable(new Gate(l, GateRule.OR, r), ruleLocation);
            case WrimeExpressionParser.XOR:
                return locatable(new Gate(l, GateRule.XOR, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }

    @Override
    public Comparison makeComparison(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.LT:
                return locatable(new Comparison(l, ComparisonRule.Less, r), ruleLocation);
            case WrimeExpressionParser.LTE:
                return locatable(new Comparison(l, ComparisonRule.LessOrEqual, r), ruleLocation);
            case WrimeExpressionParser.GT:
                return locatable(new Comparison(l, ComparisonRule.Greater, r), ruleLocation);
            case WrimeExpressionParser.GTE:
                return locatable(new Comparison(l, ComparisonRule.GreaterOrEqual, r), ruleLocation);
            case WrimeExpressionParser.EQ:
                return locatable(new Comparison(l, ComparisonRule.Equal, r), ruleLocation);
            case WrimeExpressionParser.NEQ:
                return locatable(new Comparison(l, ComparisonRule.NotEqual, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }
}
