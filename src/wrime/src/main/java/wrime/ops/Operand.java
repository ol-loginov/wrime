package wrime.ops;

import wrime.util.TypeName;

public abstract class Operand {
    private TypeName result;
    private boolean statement = true;

    public boolean isStatement() {
        return statement;
    }

    public TypeName getResult() {
        return result;
    }

    public void setResult(TypeName result) {
        this.result = result;
    }
}
