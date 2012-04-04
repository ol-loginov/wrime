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
        } else if (request.emitter instanceof StringValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof Group) {
            matchTypes0((Group) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Inverter) {
            matchTypes0((Inverter) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Oppositer) {
            matchTypes0((Oppositer) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Comparison) {
            matchTypes0((Comparison) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Algebraic) {
            matchTypes0((Algebraic) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Funcall) {
            matchTypes0((Funcall) request.emitter, scope, request.firstPass);
        } else {
            throw new WrimeException("No way to match emitter of type " + request.emitter.getClass(), null);
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

    private void matchTypes0(Comparison emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getLeft()));
            emittersToMatch.push(new MatchRequest(emitter.getRight()));
        } else {
            requireNumberReturnType(emitter.getLeft(), "required for comparison " + emitter.getRule());
            requireNumberReturnType(emitter.getRight(), "required for comparison " + emitter.getRule());
            emitter.setReturnType(new TypeName(boolean.class));
        }
    }

    private void matchTypes0(Oppositer emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getInner()));
        } else {
            requireNumberReturnType(emitter.getInner(), "required for negative expression");
            emitter.setReturnType(emitter.getInner().getReturnType());
        }
    }

    private void matchTypes0(Algebraic emitter, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            emittersToMatch.push(new MatchRequest(emitter.getLeft()));
            emittersToMatch.push(new MatchRequest(emitter.getRight()));
        } else {
            requireNumberReturnType(emitter.getLeft(), "required for algebraic " + emitter.getRule());
            requireNumberReturnType(emitter.getRight(), "required for algebraic " + emitter.getRule());
            emitter.setReturnType(getWidestNumberType(emitter.getLeft().getReturnType(), emitter.getRight().getReturnType()));
        }
    }

    private void matchTypes0(Funcall emitter, ExpressionScope scope, boolean firstPass) {
        if (firstPass) {
            emittersToMatch.push(new MatchRequest(emitter, false));
            if (emitter.getInvocable() != null) {
                emittersToMatch.push(new MatchRequest(emitter.getInvocable()));
            }
            if (emitter.hasArguments()) {
                for (Emitter argument : emitter.getArguments()) {
                    emittersToMatch.push(new MatchRequest(argument));
                }
            }
        } else {

        }
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

    private void requireNumberReturnType(Emitter emitter, String need) {
        if (!isAnyNumber(emitter.getReturnType())) {
            throw new WrimeException("component is not of number type (" + need + ")", null, emitter.getLocation());
        }
    }

    private TypeName getWidestNumberType(TypeName a, TypeName b) {
        return getNumberTypeWeight(a) >= getNumberTypeWeight(b) ? a : b;
    }

    private int getNumberTypeWeight(TypeName a) {
        if (byte.class.equals(a.getType()) || Byte.TYPE.equals(a.getType()))
            return 0;
        if (short.class.equals(a.getType()) || Short.TYPE.equals(a.getType()))
            return 1;
        if (int.class.equals(a.getType()) || Integer.TYPE.equals(a.getType()))
            return 2;
        if (long.class.equals(a.getType()) || Long.TYPE.equals(a.getType()))
            return 3;
        if (float.class.equals(a.getType()) || Float.TYPE.equals(a.getType()))
            return 4;
        if (double.class.equals(a.getType()) || Double.TYPE.equals(a.getType()))
            return 5;
        throw new WrimeException("cannot work with number type " + a, null);
    }

    private boolean isAnyNumber(TypeName type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return byte.class.equals(type.getType()) || Byte.TYPE.equals(type.getType())
                || short.class.equals(type.getType()) || Short.TYPE.equals(type.getType())
                || int.class.equals(type.getType()) || Integer.TYPE.equals(type.getType())
                || long.class.equals(type.getType()) || Long.TYPE.equals(type.getType())
                || float.class.equals(type.getType()) || Float.TYPE.equals(type.getType())
                || double.class.equals(type.getType()) || Double.TYPE.equals(type.getType());
    }

    private boolean isBoolean(TypeName type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return boolean.class.equals(type.getType()) || Boolean.TYPE.equals(type.getType());
    }
}
