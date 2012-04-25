package wrime.tags;

import wrime.WrimeException;
import wrime.ast.*;
import wrime.ast.StringValue;
import wrime.bytecode.ExpressionScope;
import wrime.bytecode.ExpressionStack;
import wrime.lang.TypeName;
import wrime.lang.TypeUtil;
import wrime.lang.TypeWrap;

import java.lang.reflect.Method;
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
        TypeName functorType = scope.getFunctorType(emitter.getName());
        if (functorType == null) {
            throw new WrimeException("No functor '" + emitter.getName() + "' defined at a point", null, emitter.getLocation());
        }
        emitter.setReturnType(functorType);
    }

    private void doMatchTypes(VariableRef emitter, ExpressionScope scope) {
        TypeName varType = scope.getVarType(emitter.getName());
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
            emitter.setReturnType(new TypeName(boolean.class));
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
            emitter.setReturnType(new TypeName(boolean.class));
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
            emitter.setReturnType(new TypeName(boolean.class));
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

            List<TypeName> argumentTypes = new ArrayList<TypeName>();
            if (emitter.hasArguments()) {
                for (Emitter argument : emitter.getArguments()) {
                    requireReturnType(argument, "required for method identification");
                    argumentTypes.add(argument.getReturnType());
                }
            }

            Method method = TypeUtil.findMethodOrGetter(
                    invocable.getReturnType(),
                    emitter.getMethodName(),
                    argumentTypes.toArray(new TypeName[argumentTypes.size()]));
            if (method == null) {
                throw new WrimeException("No suitable method '" + emitter.getMethodName() + "' found in type " + invocable.getReturnType(), null, emitter.getLocation());
            }
            emitter.setInvocation(method);
            emitter.setReturnType(TypeUtil.createReturnTypeDef(method));
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
        if (!TypeWrap.create(emitter.getReturnType().getType()).isAssignableTo(clazz)) {
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

    private TypeName getWidestNumberType(TypeName a, TypeName b) {
        return getNumberTypeWeight(a) >= getNumberTypeWeight(b) ? a : b;
    }

    private int getNumberTypeWeight(TypeName a) {
        if (byte.class.equals(a.getType()) || Byte.class.equals(a.getType()))
            return 0;
        if (short.class.equals(a.getType()) || Short.class.equals(a.getType()))
            return 1;
        if (int.class.equals(a.getType()) || Integer.class.equals(a.getType()))
            return 2;
        if (long.class.equals(a.getType()) || Long.class.equals(a.getType()))
            return 3;
        if (float.class.equals(a.getType()) || Float.class.equals(a.getType()))
            return 4;
        if (double.class.equals(a.getType()) || Double.class.equals(a.getType()))
            return 5;
        throw new WrimeException("cannot work with number type " + a, null);
    }

    private boolean isAnyNumber(TypeName type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return byte.class.equals(type.getType()) || Byte.class.equals(type.getType())
                || short.class.equals(type.getType()) || Short.class.equals(type.getType())
                || int.class.equals(type.getType()) || Integer.class.equals(type.getType())
                || long.class.equals(type.getType()) || Long.class.equals(type.getType())
                || float.class.equals(type.getType()) || Float.class.equals(type.getType())
                || double.class.equals(type.getType()) || Double.class.equals(type.getType());
    }

    private boolean isBoolean(TypeName type) {
        if (type == null || type.isNullType()) {
            return false;
        }
        return boolean.class.equals(type.getType()) || Boolean.class.equals(type.getType());
    }
}
