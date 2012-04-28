package wrime.reflect.model;

@SuppressWarnings("UnusedDeclaration")
public class GenericMethods {
    public <T> T unresolvable(int val) {
        return null;
    }

    public <T> String foo(T arg) {
        return "";
    }

    public <T> T foo2(T arg) {
        return arg;
    }

    public <T extends I1> T foo2(T arg) {
        return arg;
    }

    public <T extends I2> T foo2(T arg) {
        return arg;
    }

    public <T extends I1> void varargI1(T... all) {
    }

    public <T extends I2 & I1> T varargI1I2(T... all) {
        return null;
    }
}
