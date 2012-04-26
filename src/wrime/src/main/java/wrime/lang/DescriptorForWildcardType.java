package wrime.lang;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class DescriptorForWildcardType extends TypeDescriptorImpl {
    final WildcardType type;

    public DescriptorForWildcardType(WildcardType type) {
        this.type = type;
    }

    @Override
    public WildcardType getType() {
        return type;
    }

    @Override
    public String getJavaSourceName() {
        Type[] uppers = type.getUpperBounds();
        if (uppers == null || uppers.length != 1) {
            throw new IllegalStateException("many upper bounds is not implemented");
        }
        return TypeDescriptorImpl.create(uppers[0]).getJavaSourceName();
    }
}
