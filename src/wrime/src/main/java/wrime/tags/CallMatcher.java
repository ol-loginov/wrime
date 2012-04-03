package wrime.tags;

import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.util.ExpressionContextKeeper;
import wrime.util.TypeName;

import java.util.Stack;

/**
 * Resolves emitter input and output types
 */
public class CallMatcher {
    private final Emitter emitter;

    public CallMatcher(Emitter emitter) {
        if (emitter == null) {
            throw new IllegalArgumentException("emitter is null");
        }
        this.emitter = emitter;
    }

    public void matchTypes(ExpressionContextKeeper scope) {
        Stack<Emitter> emittersToMatch = new Stack<Emitter>();
        emittersToMatch.push(emitter);

        while (!emittersToMatch.empty()) {
            matchTypes(emittersToMatch, emittersToMatch.pop(), scope);
        }
    }

    private void matchTypes(Stack<Emitter> emitters, Emitter emitter, ExpressionContextKeeper scope) {
        if (emitter instanceof Gate) {
            matchTypes0(emitters, (Gate) emitter, scope);
        } else if (emitter instanceof NumberValue) {
            matchTypes0(emitters, (NumberValue) emitter, scope);
        } else if (emitter instanceof BoolValue) {
            matchTypes0(emitters, (BoolValue) emitter, scope);
        } else if (emitter instanceof NullValue) {
            matchTypes0(emitters, (NullValue) emitter, scope);
        } else if (emitter instanceof Group) {
            matchTypes0(emitters, (Group) emitter, scope);
        } else if (emitter instanceof Inverter) {
            matchTypes0(emitters, (Inverter) emitter, scope);
        } else if (emitter instanceof Comparison) {
            matchTypes0(emitters, (Comparison) emitter, scope);
        } else if (emitter instanceof Algebraic) {
            matchTypes0(emitters, (Algebraic) emitter, scope);
        } else if (emitter instanceof StringValue) {
            matchTypes0(emitters, (StringValue) emitter, scope);
        } else if (emitter instanceof Func) {
            matchTypes0(emitters, (Func) emitter, scope);
        } else {
            throw new WrimeException("No way to write emitter of type " + emitter.getClass(), null);
        }
    }

    private void matchTypes0(Stack<Emitter> emitters, Gate emitter, ExpressionContextKeeper scope) {
        if (!emitter.isReturnTypeResolvable()) {
            emitters.push(emitter);
            emitters.push(emitter.getLeft());
            emitters.push(emitter.getRight());
        } else {
            requireBooleanReturnType(emitter.getLeft());
            requireBooleanReturnType(emitter.getRight());
            emitter.setReturnType(new TypeName(boolean.class));
        }
    }

    private void matchTypes0(Stack<Emitter> emitters, NumberValue emitter, ExpressionContextKeeper scope) {
        emitter.setReturnType(new TypeName(int.class));
    }

    private void matchTypes0(Stack<Emitter> emitters, BoolValue emitter, ExpressionContextKeeper scope) {
        emitter.setReturnType(new TypeName(boolean.class));
    }

    private void matchTypes0(Stack<Emitter> emitters, NullValue emitter, ExpressionContextKeeper scope) {
        emitter.setReturnType(TypeName.NULL_TYPE);
    }

    private void matchTypes0(Stack<Emitter> emitters, Group emitter, ExpressionContextKeeper scope) {
        emitters.push(emitter.getInner());
    }

    private void matchTypes0(Stack<Emitter> emitters, Inverter emitter, ExpressionContextKeeper scope) {
        if (!emitter.isReturnTypeResolvable()) {
            emitters.push(emitter);
            emitters.push(emitter.getInner());
        } else {
            requireBooleanReturnType(emitter.getInner());
            emitter.setReturnType(new TypeName(boolean.class));
        }
    }

    private void matchTypes0(Stack<Emitter> emitters, Comparison emitter, ExpressionContextKeeper scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, Algebraic emitter, ExpressionContextKeeper scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, StringValue emitter, ExpressionContextKeeper scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, Func emitter, ExpressionContextKeeper scope) {
    }

    private void requireBooleanReturnType(Emitter emitter) {
        if (!isBoolean(emitter.getReturnType())) {
            throw new WrimeException("Expression is not of boolean type (required for gate)", null, emitter.getLocation());
        }
    }

    private boolean isBoolean(TypeName typeName) {
        return false;
    }
}
