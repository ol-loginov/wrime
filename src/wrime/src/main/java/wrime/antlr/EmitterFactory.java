package wrime.antlr;

import org.antlr.runtime.Token;
import wrime.Location;
import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;

import java.util.List;

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
    public NumberValue getNumber(Token token) {
        return locatable(new NumberValue(token.getText()), token);
    }

    @Override
    public BoolValue getBool(Token token, boolean value) {
        return locatable(new BoolValue(value), token);
    }

    @Override
    public NullValue getNull(Token token) {
        return locatable(new NullValue(), token);
    }

    @Override
    public StringValue getString(Token token) {
        return locatable(new StringValue(token.getText()), token);
    }

    @Override
    public Name getName(Token token) {
        return locatable(new Name(token.getText()), token);
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

    @Override
    public Algebraic makeMath(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.PLUS:
                return locatable(new Algebraic(l, AlgebraicRule.PLUS, r), ruleLocation);
            case WrimeExpressionParser.MINUS:
                return locatable(new Algebraic(l, AlgebraicRule.MINUS, r), ruleLocation);
            case WrimeExpressionParser.STAR:
                return locatable(new Algebraic(l, AlgebraicRule.MUL, r), ruleLocation);
            case WrimeExpressionParser.DIV:
                return locatable(new Algebraic(l, AlgebraicRule.DIV, r), ruleLocation);
            case WrimeExpressionParser.MOD:
                return locatable(new Algebraic(l, AlgebraicRule.MOD, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }

    @Override
    public Group makeGroup(Token o, Emitter e) {
        return locatable(new Group(e), o);
    }

    @Override
    public Inverter makeInversion(Token o, Emitter e) {
        return locatable(new Inverter(e), o);
    }

    @Override
    public Func makeFunc(String functor, List<Name> memberPath, List<Emitter> arguments) {
        return new Func(functor, new NamePath(memberPath), arguments);
    }
}
