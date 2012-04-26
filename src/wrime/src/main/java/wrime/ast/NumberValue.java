package wrime.ast;

import wrime.WrimeException;
import wrime.lang.TypeDef;

public class NumberValue extends Emitter {
    private final String text;

    public NumberValue(String text) {
        this.text = text;

        long longVal = 0;
        boolean longValFailed = false;
        try {
            longVal = Long.parseLong(text);
        } catch (NumberFormatException nfe) {
            longValFailed = true;
        }

        if (!longValFailed) {
            if (longVal >= Integer.MAX_VALUE || longVal <= Integer.MIN_VALUE) {
                setReturnType(new TypeDef(long.class));
            } else {
                setReturnType(new TypeDef(int.class));
            }
            return;
        }

        double doubleVal = 0;
        boolean doubleValFailed = false;
        try {
            doubleVal = Double.parseDouble(text);
        } catch (NumberFormatException nfe) {
            doubleValFailed = true;
        }


        if (!doubleValFailed) {
            if (doubleVal >= Float.MAX_VALUE || doubleVal <= Float.MIN_VALUE) {
                setReturnType(new TypeDef(double.class));
            } else {
                setReturnType(new TypeDef(float.class));
            }
            return;
        }

        throw new WrimeException("cannot translate " + text + " to number", null);
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
