package wrime.ast;

import java.util.List;

public class ClassName {
    private LocatableString className;
    private List<ClassName> genericTypes;
    private List<LocatableString> packageName;

    public void setGenericTypes(List<ClassName> genericTypes) {
        this.genericTypes = genericTypes;
    }

    public LocatableString getClassName() {
        return className;
    }

    public void setClassName(LocatableString className) {
        this.className = className;
    }

    public List<LocatableString> getPackageName() {
        return packageName;
    }

    public void setPackageName(List<LocatableString> packageName) {
        this.packageName = packageName;
    }
}
