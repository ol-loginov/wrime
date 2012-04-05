package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagImport;
import wrime.ast.WrimeTag;

public class ImportTagFactory implements TagFactory {
    @Override
    public ImportTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new ImportTagProcessor((TagImport) tag);
    }
}
