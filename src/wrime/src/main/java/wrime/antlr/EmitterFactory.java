package wrime.antlr;

import org.antlr.runtime.Token;
import wrime.Location;
import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.reflect.ClassName;

import java.util.List;

public class EmitterFactory implements WrimeExpressionParser.EmitterFactory {
    private final Location location;

    public EmitterFactory(Location location) {
        this.location = location;
    }

    private <T extends Locatable> T locatable(T emitter, Location location) {
        emitter.setLocation(location);
        return emitter;
    }

    private <T extends Locatable> T locatable(T emitter, Token token) {
        return locatable(emitter, getLocation(token));
    }

    @Override
    public Location getLocation(Token token) {
        return location.move(token.getLine(), token.getCharPositionInLine());
    }

    @Override
    public NumberValue getNumber(Token token) {
        try {
            return locatable(new NumberValue(token.getText()), token);
        } catch (WrimeException we) {
            throw new WrimeException(we.getMessage(), null, getLocation(token));
        }
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
    public LocatableString getLocatableString(Token n) {
        return locatable(new LocatableString(n.getText()), n);
    }

    @Override
    public ClassName getClassName(String packageName, LocatableString className) {
        ClassName result = new ClassName();
        result.setClassName(className.getText());
        result.setPackageName(packageName);
        return result;
    }

    @Override
    public Gate makeGate(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.AND:
                return locatable(new Gate(l, Gate.Rule.AND, r), ruleLocation);
            case WrimeExpressionParser.OR:
                return locatable(new Gate(l, Gate.Rule.OR, r), ruleLocation);
            case WrimeExpressionParser.XOR:
                return locatable(new Gate(l, Gate.Rule.XOR, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }

    @Override
    public Comparison makeComparison(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.LT:
                return locatable(new Comparison(l, Comparison.Rule.Less, r), ruleLocation);
            case WrimeExpressionParser.LTE:
                return locatable(new Comparison(l, Comparison.Rule.LessOrEqual, r), ruleLocation);
            case WrimeExpressionParser.GT:
                return locatable(new Comparison(l, Comparison.Rule.Greater, r), ruleLocation);
            case WrimeExpressionParser.GTE:
                return locatable(new Comparison(l, Comparison.Rule.GreaterOrEqual, r), ruleLocation);
            case WrimeExpressionParser.EQ:
                return locatable(new Comparison(l, Comparison.Rule.Equal, r), ruleLocation);
            case WrimeExpressionParser.NEQ:
                return locatable(new Comparison(l, Comparison.Rule.NotEqual, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }

    @Override
    public Algebraic makeMath(Emitter l, Token o, Emitter r) {
        Location ruleLocation = location.move(o.getLine(), o.getCharPositionInLine());
        switch (o.getType()) {
            case WrimeExpressionParser.PLUS:
                return locatable(new Algebraic(l, Algebraic.Rule.PLUS, r), ruleLocation);
            case WrimeExpressionParser.MINUS:
                return locatable(new Algebraic(l, Algebraic.Rule.MINUS, r), ruleLocation);
            case WrimeExpressionParser.STAR:
                return locatable(new Algebraic(l, Algebraic.Rule.MUL, r), ruleLocation);
            case WrimeExpressionParser.DIV:
                return locatable(new Algebraic(l, Algebraic.Rule.DIV, r), ruleLocation);
            case WrimeExpressionParser.MOD:
                return locatable(new Algebraic(l, Algebraic.Rule.MOD, r), ruleLocation);
            default:
                throw new WrimeException("Unrecognized gate operation '" + o.getText() + "'", null, ruleLocation);
        }
    }

    @Override
    public Group makeGroup(Token o, Emitter e) {
        return locatable(new Group(e), o);
    }

    @Override
    public Oppositer makeOpposite(Token o, Emitter e) {
        return locatable(new Oppositer(e), o);
    }

    @Override
    public Inverter makeInversion(Token o, Emitter e) {
        return locatable(new Inverter(e), o);
    }

    @Override
    public VariableRef makeVariableAccess(LocatableString name) {
        return locatable(new VariableRef(name.getText()), name.getLocation());
    }

    @Override
    public FunctorRef makeFunctorAccess(LocatableString name) {
        return locatable(new FunctorRef(name.getText()), name.getLocation());
    }

    @Override
    public MethodCall makeMethodCall(Emitter invocable, LocatableString method, List<Emitter> arguments) {
        return locatable(new MethodCall(invocable, method.getText(), arguments), method.getLocation());
    }

    @Override
    public Assignment makeAssignment(LocatableString varName) {
        Assignment assignment = new Assignment();
        assignment.setVar(varName);
        return assignment;
    }

    @Override
    public TagBreak makeTagBreak(Location location) {
        return locatable(new TagBreak(), location);
    }

    @Override
    public TagContinue makeTagContinue(Location location) {
        return locatable(new TagContinue(), location);
    }

    @Override
    public TagIf makeTagIfClose(Location location) {
        return locatable(new TagIf(TagIf.Mode.CLOSE), location);
    }

    @Override
    public TagIf makeTagIfElse(Location location) {
        return locatable(new TagIf(TagIf.Mode.ELSE), location);
    }

    @Override
    public TagIf makeTagIfElif(Location location, Emitter emitter) {
        return locatable(new TagIf(TagIf.Mode.ELIF, emitter), location);
    }

    @Override
    public TagIf makeTagIf(Location location, Emitter emitter) {
        return locatable(new TagIf(TagIf.Mode.OPEN, emitter), location);
    }

    @Override
    public TagFor makeTagFor(Location location) {
        return locatable(new TagFor(), location);
    }

    @Override
    public TagInclude makeTagInclude(Location location, Emitter source) {
        return locatable(new TagInclude(source), location);
    }

    @Override
    public TagImport makeTagImport(Location location, String packagePath, LocatableString packageName) {
        return locatable(new TagImport(packagePath, packageName), location);
    }

    @Override
    public TagSet makeTagSet(Location location, LocatableString var, Emitter e) {
        return locatable(new TagSet(var, e), location);
    }

    @Override
    public TagParam makeTagParam(Location location, ClassName className, LocatableString paramName) {
        return locatable(new TagParam(className, paramName), location);
    }

    @Override
    public TagCustom makeCustomTag(Location location, LocatableString name, List<Emitter> arguments) {
        return locatable(new TagCustom(name.getText(), arguments), location);
    }
}
