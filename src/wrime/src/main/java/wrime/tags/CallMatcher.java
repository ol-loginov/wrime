package wrime.tags;

import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.bytecode.ExpressionScope;
import wrime.bytecode.ExpressionStack;
import wrime.lang.MethodDef;
import wrime.lang.MethodLookup;
import wrime.lang.TypeDef;

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
        TypeDef functorType = scope.getFunctorType(emitter.getName());
        if (functorType == null) {
            throw new WrimeException("No functor '" + emitter.getName() + "' defined at a point", null, emitter.getLocation());
        }
        emitter.setReturnType(functorType);
    }

    private void doMatchTypes(VariableRef emitter, ExpressionScope scope) {
        TypeDef varType = scope.getVarType(emitter.getName());
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
            emitter.setReturnType(new TypeDef(boolean.class));
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
            emitter.setReturnType(new TypeDef(boolean.class));
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
            emitter.setReturnType(new TypeDef(boolean.class));
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

            List<TypeDef> argumentTypes = new ArrayList<TypeDef>();
            if (emitter.hasArguments()) {
                for (Emitter argument : emitter.getArguments()) {
                    requireReturnType(argument, "required for method identification");
                    argumentTypes.add(argument.getReturnType());
                }
            }

            MethodDef method = MethodLookup.findInvoker(
                    invocable.getReturnType(),
                    emitter.getMethodName(),
                    argumentTypes.toArray(new TypeDef[argumentTypes.size()]));
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
        if (emitter.getReturnType() == null) {
            throw new WrimeException("component has no defined return type (" + need + ")", null, emitter.getLocation());
        }
    }

    public static void requireNoVoidType(Emitter emitter, String need) {
        if (emitter.getReturnType() == null || emitter.getReturnType().isVoid()) {
            throw new WrimeException("component has no defined return type (" + need + ")", null, emitter.getLocation());
        }
    }

    public static void requireReturnType(Emitter emitter, Class clazz, String need) {
        requireReturnType(emitter, need);
        if (!new TypeDef(clazz).isAssignableFrom(emitter.getReturnType())) {
            throw new WrimeException("statement is not of type needed (" + need + ")", null, emitter.getLocation());
        }
    }

    public void requireBooleanReturnType(String need) {
        if (!isBoolean(root.getReturnType())) {
            throw new WrimeException("component is not of boolean type (" + need + ")", null, root.getLocation());
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

    private TypeDef getWidestNumberType(TypeDef a, TypeDef b) {
        return getNumberTypeWeight(a) >= getNumberTypeWeight(b) ? a : b;
    }

    private int getNumberTypeWeight(TypeDef a) {
        if (a.isA(byte.class) || a.isA(Byte.class))
            return 0;
        if (a.isA(short.class) || a.isA(Short.class))
            return 1;
        if (a.isA(int.class) || a.isA(Integer.class))
            return 2;
        if (a.isA(long.class) || a.isA(Long.class))
            return 3;
        if (a.isA(float.class) || a.isA(Float.class))
            return 4;
        if (a.isA(double.class) || a.isA(Double.class))
            return 5;
        throw new WrimeException("cannot work with number type " + a, null);
    }

    private boolean isAnyNumber(TypeDef type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return type.isA(byte.class) || type.isA(Byte.class)
                || type.isA(short.class) || type.isA(Short.class)
                || type.isA(int.class) || type.isA(Integer.class)
                || type.isA(long.class) || type.isA(Long.class)
                || type.isA(float.class) || type.isA(Float.class)
                || type.isA(double.class) || type.isA(Double.class);
    }

    private boolean isBoolean(TypeDef type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return type.isA(boolean.class) || type.isA(Boolean.class);
    }
}
