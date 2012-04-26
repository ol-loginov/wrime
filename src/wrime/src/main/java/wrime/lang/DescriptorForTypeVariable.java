package wrime.lang;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class DescriptorForTypeVariable extends TypeDescriptorImpl {
    final TypeVariable type;

    public DescriptorForTypeVariable(TypeVariable type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getJavaSourceName() {
        return new DescriptorForClass(Object.class).getJavaSourceName();
    }
}