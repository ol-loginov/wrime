package wrime.reflect.model;

public class GenericMethods {
    public <T> T unresolvable(int val) {
        return null;
    }

    public <T> String foo(T arg) {
        return "";
    }

    public <T extends Enum> T foo2(T arg) {
        return arg;
    }

    public <T extends I1> T foo2(T arg) {
        return arg;
    }

    public <T extends I2> T foo2(T arg) {
        return arg;
    }
}
