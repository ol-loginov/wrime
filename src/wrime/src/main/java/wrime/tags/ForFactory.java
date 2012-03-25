package wrime.tags;

public class ForFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "for".equals(name);
    }

    @Override
    public ForReceiver createReceiver(String name) {
        return new ForReceiver();
    }
}
