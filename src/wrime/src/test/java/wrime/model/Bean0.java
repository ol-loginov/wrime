package wrime.model;

public class Bean0 {
    private String hello;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public String arg1() {
        return "arg";
    }

    public String arg1(String arg) {
        return arg;
    }

    public String arg2(String arg1, String arg2) {
        return arg1 + arg2;
    }
}
