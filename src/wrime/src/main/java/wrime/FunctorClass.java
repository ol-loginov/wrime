package wrime;

public class FunctorClass {
    private String functorId;
    private Class functorType;
    private Object functorInstance;

    public String getFunctorId() {
        return functorId;
    }

    public void setFunctorId(String functorId) {
        this.functorId = functorId;
    }

    public Class getFunctorType() {
        return functorType;
    }

    public void setFunctorType(Class functorType) {
        this.functorType = functorType;
    }

    public Object getFunctorInstance() {
        return functorInstance;
    }

    public void setFunctorInstance(Object functorInstance) {
        this.functorInstance = functorInstance;
    }
}
