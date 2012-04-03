package wrime.ast;

public abstract class WrimeTag extends Locatable {
    private final boolean customTag;
    private final String processor;

    WrimeTag(String processor) {
        this(processor, false);
    }

    WrimeTag(String processor, boolean customTag) {
        this.customTag = customTag;
        this.processor = processor;
    }

    public boolean isCustomTag() {
        return customTag;
    }

    public String getProcessor() {
        return processor;
    }
}
