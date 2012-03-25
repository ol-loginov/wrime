package wrime.model;

public class IfBean {
    public boolean getBool() {
        return true;
    }

    public Boolean getBoolean() {
        return null;
    }

    public IfBean getReference() {
        return this;
    }

    public void getVoid() {
    }
}
