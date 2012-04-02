package wrime.ast;

public class LocatableString extends Locatable {
    private final String text;

    public LocatableString(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
