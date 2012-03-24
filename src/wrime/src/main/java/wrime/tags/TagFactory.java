package wrime.tags;

public interface TagFactory {
    boolean supports(String name);

    PathReceiver createReceiver(String name);
}
