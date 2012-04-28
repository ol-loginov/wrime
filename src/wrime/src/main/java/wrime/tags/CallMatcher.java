package wrime.tags;

import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.bytecode.ExpressionScope;
import wrime.bytecode.ExpressionStack;
import wrime.reflect.MethodLookup;
import wrime.reflect.MethodLookuper;
import wrime.reflect.TypeConverter;
import wrime.reflect.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Resolves emitter input and output types
 */
public class CallMatcher {
    private final Stack<MatchRequest> emittersToMatch;
    private final Emitter root;

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

    public CallMatcher(Emitter emitter) {
        if (emitter == null) {
            throw new IllegalArgumentException("emitter is null");
        }
        this.root = emitter;
        this.emittersToMatch = new Stack<MatchRequest>() {{
            push(new MatchRequest(root));
        }};
    }

    private void matchNeeded(Emitter emitter) {
        emittersToMatch.push(new MatchRequest(emitter));
    }

    private void matchLater(Emitter emitter) {
        emittersToMatch.push(new MatchRequest(emitter, false));
    }

    public CallMatcher matchTypes(ExpressionStack scope) {
        while (!emittersToMatch.empty()) {
            matchTypes(emittersToMatch.pop(), scope);
        }
        return this;
    }

    private void matchTypes(MatchRequest request, ExpressionStack scope) {
        if (request.emitter instanceof Gate) {
            doMatchTypes((Gate) request.emitter, request.firstPass);
        } else if (request.emitter instanceof NumberValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof BoolValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof NullValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof StringValue) {
            requireReturnType(request.emitter, "should be hardcoded");
        } else if (request.emitter instanceof Group) {
            doMatchTypes((Group) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Inverter) {
            doMatchTypes((Inverter) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Oppositer) {
            doMatchTypes((Oppositer) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Comparison) {
            doMatchTypes((Comparison) request.emitter, request.firstPass);
        } else if (request.emitter instanceof Algebraic) {
            doMatchTypes((Algebraic) request.emitter, request.firstPass);
        } else if (request.emitter instanceof MethodCall) {
            doMatchTypes((MethodCall) request.emitter, request.firstPass);
        } else if (request.emitter instanceof VariableRef) {
            doMatchTypes((VariableRef) request.emitter, scope.current());
        } else if (request.emitter instanceof FunctorRef) {
            doMatchTypes((FunctorRef) request.emitter, scope);
        } else {
            throw new WrimeException("No way to match emitter of type " + request.emitter.getClass(), null);
        }
    }

    private void doMatchTypes(FunctorRef emitter, ExpressionStack scope) {
        Type functorType = scope.getFunctorType(emitter.getName());
        if (functorType == null) {
            throw new WrimeException("No functor '" + emitter.getName() + "' defined at a point", null, emitter.getLocation());
        }
        emitter.setReturnType(functorType);
    }

    private void doMatchTypes(VariableRef emitter, ExpressionScope scope) {
        Type varType = scope.getVarType(emitter.getName());
        if (varType == null) {
            throw new WrimeException("No variable '" + emitter.getName() + "' defined at a point", null, emitter.getLocation());
        }
        emitter.setReturnType(varType);
    }

    private void doMatchTypes(Gate emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getLeft());
            matchNeeded(emitter.getRight());
        } else {
            requireBooleanReturnType(emitter.getLeft(), "required for gate " + emitter.getRule());
            requireBooleanReturnType(emitter.getRight(), "required for gate " + emitter.getRule());
            emitter.setReturnType(boolean.class);
        }
    }

    private void doMatchTypes(Group emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getInner());
        } else {
            requireReturnType(emitter.getInner(), "required for inner expression");
        }
    }

    private void doMatchTypes(Inverter emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getInner());
        } else {
            requireBooleanReturnType(emitter.getInner(), "required for inverting expression");
            emitter.setReturnType(boolean.class);
        }
    }

    private void doMatchTypes(Comparison emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getLeft());
            matchNeeded(emitter.getRight());
        } else {
            requireNoVoidType(emitter.getLeft(), "required for comparison " + emitter.getRule());
            requireNoVoidType(emitter.getRight(), "required for comparison " + emitter.getRule());
            emitter.setReturnType(boolean.class);
        }
    }

    private void doMatchTypes(Oppositer emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getInner());
        } else {
            requireNumberReturnType(emitter.getInner(), "required for negative expression");
            emitter.setReturnType(emitter.getInner().getReturnType());
        }
    }

    private void doMatchTypes(Algebraic emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getLeft());
            matchNeeded(emitter.getRight());
        } else {
            requireNumberReturnType(emitter.getLeft(), "required for algebraic " + emitter.getRule());
            requireNumberReturnType(emitter.getRight(), "required for algebraic " + emitter.getRule());
            emitter.setReturnType(getWidestNumberType(emitter.getLeft().getReturnType(), emitter.getRight().getReturnType()));
        }
    }

    private void doMatchTypes(MethodCall emitter, boolean firstPass) {
        if (firstPass) {
            matchLater(emitter);
            matchNeeded(emitter.getInvocable());
            if (emitter.hasArguments()) {
                for (Emitter argument : emitter.getArguments()) {
                    matchNeeded(argument);
                }
            }
        } else {
            Emitter invocable = emitter.getInvocable();
            requireReturnType(invocable, "required for method identification");

            List<Type> argumentTypes = new ArrayList<Type>();
            if (emitter.hasArguments()) {
                for (Emitter argument : emitter.getArguments()) {
                    requireReturnType(argument, "required for method identification");
                    argumentTypes.add(argument.getReturnType());
                }
            }

            MethodLookup method = MethodLookuper.findInvoker(
                    invocable.getReturnType(),
                    emitter.getMethodName(),
                    argumentTypes.toArray(new Type[argumentTypes.size()]));
            if (method == null) {
                throw new WrimeException("No suitable method '" + emitter.getMethodName() + "' found in type " + invocable.getReturnType(), null, emitter.getLocation());
            }
            emitter.setInvocation(method);
            emitter.setReturnType(method.getReturnType());
        }
    }

    public void requireReturnType(String need) {
        requireReturnType(root, need);
    }

    public static void requireReturnType(Emitter emitter, String need) {
        if (emitter.getReturnType() == null || emitter.getReturnType() == Types.NULL_TYPE) {
            throw new WrimeException("component has no defined return type (" + need + ")", null, emitter.getLocation());
        }
    }

    public static void requireNoVoidType(Emitter emitter, String need) {
        if (emitter.getReturnType() == null || emitter.getReturnType() == Types.NULL_TYPE || Types.isOneOf(emitter.getReturnType(), Void.TYPE)) {
            throw new WrimeException("component has no defined return type (" + need + ")", null, emitter.getLocation());
        }
    }

    public static void requireReturnType(Emitter emitter, Class clazz, String need) {
        requireReturnType(emitter, need);
        if (!TypeConverter.isAssignable(clazz, emitter.getReturnType())) {
            throw new WrimeException("statement is not of type needed (" + need + ")", null, emitter.getLocation());
        }
    }

    public void requireBooleanReturnType(String need) {
        if (!Types.isBoolean(root.getReturnType())) {
            throw new WrimeException("component is not of boolean type (" + need + ")", null, root.getLocation());
        }
    }

    private void requireBooleanReturnType(Emitter emitter, String need) {
        if (!Types.isBoolean(emitter.getReturnType())) {
            throw new WrimeException("component is not of boolean type (" + need + ")", null, emitter.getLocation());
        }
    }

    private void requireNumberReturnType(Emitter emitter, String need) {
        if (!Types.isAnyNumber(emitter.getReturnType())) {
            throw new WrimeException("component is not of number type (" + need + ")", null, emitter.getLocation());
        }
    }

    private Type getWidestNumberType(Type a, Type b) {
        return Types.getNumberTypeWeight(a) >= Types.getNumberTypeWeight(b) ? a : b;
    }
}
