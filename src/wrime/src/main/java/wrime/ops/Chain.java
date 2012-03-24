package wrime.ops;

import java.util.ArrayList;
import java.util.List;

public class Chain extends Operand {
    private List<Operand> operands = new ArrayList<Operand>();

    public List<Operand> getOperands() {
        return operands;
    }

    @Override
    public boolean isStatement() {
        return operands.size() > 0 && operands.get(operands.size() - 1).isStatement();
    }
}
