package wrime.ops;

import java.lang.reflect.Method;

public class Getter extends Operand {
    private Operand invocable;
    private String propName;
    private Method propMethod;

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public Operand getInvocable() {
        return invocable;
    }

    public void setInvocable(Operand invocable) {
        this.invocable = invocable;
    }

    public Method getPropMethod() {
        return propMethod;
    }

    public void setPropMethod(Method propMethod) {
        this.propMethod = propMethod;
    }
}
