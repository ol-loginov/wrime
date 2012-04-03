package wrime.tags;

import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.util.ExpressionScope;
import wrime.util.TypeName;

import java.util.Stack;

/**
 * Resolves emitter input and output types
 */
public class CallMatcher {
    private final Stack<MatchRequest> emittersToMatch;


    private static class MatchRequest {
        public final Emitter emitter;
        public final boolean firstPass;

        public MatchRequest(Emitter emitter) {
            this(emitter, true);
        }

        public MatchRequest(Emitter emitter, boolean firstPass) {
            this.emitter = emitter;
            this.firstPass = firstPass;
        }
    }

    public CallMatcher(final Emitter emitter) {
        if (emitter == null) {
            throw new IllegalArgumentException("emitter is null");
        }
        this.emittersToMatch = new Stack<MatchRequest>() {{
            push(new MatchRequest(emitter));
        }};
    }

    public void matchTypes(ExpressionScope scope) {
        while (!emittersToMatch.empty()) {
            matchTypes(emittersToMatch.pop(), scope);
        }
    }

    private void matchTypes(MatchRequest request, ExpressionScope scope) {
        if (request.emitter instanceof Gate) {
            matchTypes0((Gate) request.emitter, request.firstPass);
        } else if (request.emitter instanceof NumberValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof BoolValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof NullValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof Group) {
            matchTypes0((Group) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Inverter) {
            matchTypes0((Inverter) request.emitter, request.firstPass);
//        } else if (emitter instanceof Comparison) {
//            matchTypes0(emitters, (Comparison) emitter, scope);
//        } else if (emitter instanceof Algebraic) {
//            matchTypes0(emitters, (Algebraic) emitter, scope);
//        } else if (emitter instanceof StringValue) {
//            matchTypes0(emitters, (StringValue) emitter, scope);
//        } else if (emitter instanceof Func) {
//            matchTypes0(emitters, (Func) emitter, scope);
        } else {
            throw new WrimeException("No way to write emitter of type " + request.emitter.getClass(), null);
        }
    }

    private void matchTypes0(Gate emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getLeft()));
            emittersToMatch.push(new MatchRequest(emitter.getRight()));
        } else {
            requireBooleanReturnType(emitter.getLeft(), "required for gate " + emitter.getRule());
            requireBooleanReturnType(emitter.getRight(), "required for gate " + emitter.getRule());
            emitter.setReturnType(new TypeName(boolean.class));
        }
    }

    private void matchTypes0(Group emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getInner()));
        } else {
            requireReturnType(emitter.getInner(), "required for inner expression");
        }
    }

    private void matchTypes0(Inverter emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getInner()));
        } else {
            requireBooleanReturnType(emitter.getInner(), "required for inverting expression");
            emitter.setReturnType(new TypeName(boolean.class));
        }
    }

    private void matchTypes0(Stack<Emitter> emitters, Comparison emitter, ExpressionScope scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, Algebraic emitter, ExpressionScope scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, StringValue emitter, ExpressionScope scope) {
    }

    private void matchTypes0(Stack<Emitter> emitters, Func emitter, ExpressionScope scope) {
    }

    private void requireReturnType(Emitter emitter, String need) {
        if (emitter.getReturnType() == null) {
            throw new WrimeException("component has no defined return type (" + need + ")", null, emitter.getLocation());
        }
    }

    private void requireBooleanReturnType(Emitter emitter, String need) {
        if (!isBoolean(emitter.getReturnType())) {
            throw new WrimeException("component is not of boolean type (" + need + ")", null, emitter.getLocation());
        }
    }

    private boolean isBoolean(TypeName typeName) {
        return typeName != null
                && !typeName.isNullType()
                && (typeName.getType().equals(boolean.class) || typeName.getType().equals(Boolean.TYPE));
    }
}
