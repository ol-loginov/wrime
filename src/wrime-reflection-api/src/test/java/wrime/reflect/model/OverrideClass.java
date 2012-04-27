package wrime.reflect.model;

@SuppressWarnings("UnusedDeclaration")
public class OverrideClass {
    public String foo(String a, String b) {
        return "";
    }

    public Object foo(String a, int b) {
        return "";
    }

    public void foo(String a, Object... b) {
    }
}
