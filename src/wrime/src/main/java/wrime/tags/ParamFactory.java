package wrime.tags;

public class ParamFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "param".equals(name);
    }

    @Override
    public ParamReceiver createReceiver(String name) {
        return new ParamReceiver();
    }
}
