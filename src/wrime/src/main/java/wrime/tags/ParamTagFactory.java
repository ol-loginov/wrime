package wrime.tags;

import wrime.WrimeException;
import wrime.ast.TagParam;
import wrime.ast.WrimeTag;

public class ParamTagFactory implements TagFactory {
    @Override
    public ParamTagProcessor createProcessor(WrimeTag tag) throws WrimeException {
        return new ParamTagProcessor((TagParam) tag);
    }
}
