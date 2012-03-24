package wrime.tags;

public class ForFactory implements TagFactory {
    public static final String LOOP_SCOPE = "loop";

    @Override
    public boolean supports(String name) {
        return "for".equals(name);
    }

    @Override
    public ForReceiver createReceiver(String name) {
        return new ForReceiver();
    }
}
