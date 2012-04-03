package wrime.tags;

import wrime.WrimeException;
import wrime.ast.WrimeTag;

public interface TagFactory {
    TagProcessor createProcessor(WrimeTag tag) throws WrimeException;
}
