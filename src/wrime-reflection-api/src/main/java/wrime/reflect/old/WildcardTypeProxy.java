package wrime.reflect.old;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class WildcardTypeProxy extends TypeProxyImpl {
    final WildcardType type;

    public WildcardTypeProxy(WildcardType type) {
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
        return create(uppers[0]).getJavaSourceName();
    }
}
