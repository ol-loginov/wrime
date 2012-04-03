package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class IncludeTagFactory implements TagFactory {
    @Override
    public TagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new IncludeTagProcessor();
    }
}
