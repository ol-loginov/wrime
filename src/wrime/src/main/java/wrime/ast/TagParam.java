package wrime.ast;

import java.util.ArrayList;
import java.util.List;

public class TagParam extends WrimeTag {
    private final List<LocatableString> options = new ArrayList<LocatableString>();

    public void setOption(LocatableString value) {
        options.add(value);
    }
}
