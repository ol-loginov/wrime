package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class SetTagFactory implements TagFactory {
    @Override
    public SetTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new SetTagProcessor();
    }
}
