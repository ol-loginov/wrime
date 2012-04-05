package wrime.ast;

import java.util.List;

public class ClassName {
    private String packageName;
    private LocatableString className;
    private List<ClassName> genericTypes;

    public void setGenericTypes(List<ClassName> genericTypes) {
        this.genericTypes = genericTypes;
    }

    public LocatableString getClassName() {
        return className;
    }

    public void setClassName(LocatableString className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName == null ? "" : packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPackageName());
        builder.append(getClassName().getText());
        if (genericTypes != null && genericTypes.size() > 0) {
            builder.append("<");
            boolean first = true;
            for (ClassName type : genericTypes) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                builder.append(type.toString());
            }
            builder.append(">");
        }
        return builder.toString();
    }
}
