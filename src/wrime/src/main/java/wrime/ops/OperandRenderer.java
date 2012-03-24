package wrime.ops;

import wrime.WrimeException;

public interface OperandRenderer {
    void render(Operand operand) throws WrimeException;

    void setCustomRenderer(FunctorRenderer renderer);
}
