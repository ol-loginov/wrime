package wrime.tags;

public class IncludeFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "include".equals(name);
    }

    @Override
    public IncludeReceiver createReceiver(String name) {
        return new IncludeReceiver();
    }
}
