package wrime.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodLookup {
    public static final int NORMAL = 100;
    public static final int VARARG = 90;
    public static final int GENERIC_WITH_RESTRICTION = 50;
    public static final int GENERIC = 1;

    protected final Method method;
    protected final int depth;

    private Type returnType;
    private int weight;

    public MethodLookup(Method method, int depth) {
        this.method = method;
        this.depth = depth;
    }

    public String getName() {
        return method.getName();
    }

    public Type getReturnType() {
        return returnType;
    }

    public MethodLookup setReturnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }

    public int getDepth() {
        return depth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void decrementWeight(int weight) {
        if (this.weight > weight) {
            this.weight = weight;
        }
    }
}
