package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagIf;
import wrime.ast.WrimeTag;

public class IfTagFactory implements TagFactory {
    @Override
    public IfTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new IfTagProcessor((TagIf) tag);
    }
}
