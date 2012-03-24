package wrime.tags;

public class IfFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "if".equals(name);
    }

    @Override
    public IfReceiver createReceiver(String name) {
        return new IfReceiver();
    }
}
