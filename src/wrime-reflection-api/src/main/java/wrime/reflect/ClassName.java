package wrime.reflect;

import java.util.List;

public class ClassName {
    private String packageName;
    private String className;
    private List<ClassName> genericTypes;

    public void setGenericTypes(List<ClassName> genericTypes) {
        this.genericTypes = genericTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName == null ? "" : packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<ClassName> getGenericTypes() {
        return genericTypes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPackageName());
        builder.append(getClassName());
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
