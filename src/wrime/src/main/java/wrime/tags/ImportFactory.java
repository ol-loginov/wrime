package wrime.tags;

public class ImportFactory implements TagFactory {
    @Override
    public boolean supports(String name) {
        return "import".equals(name);
    }

    @Override
    public ImportReceiver createReceiver(String name) {
        return new ImportReceiver();
    }
}
