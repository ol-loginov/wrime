package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public class ForTagFactory implements TagFactory {
    @Override
    public ForTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new ForTagProcessor();
    }
}
