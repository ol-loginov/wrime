package wrime.reflect.old;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.TreeMap;

public class TypeVariableMap {
    private Map<String, TypeVariable> variables;
    private Map<String, Type> types;

    public TypeVariableMap() {
        variables = new TreeMap<String, TypeVariable>();
        types = new TreeMap<String, Type>();
    }

    public void put(TypeVariable variable, Type typeProxy) {
        variables.put(variable.getName(), variable);
        types.put(variable.getName(), typeProxy);
    }

    public Type getVariableType(TypeVariable variable) {
        return types.get(variable.getName());
    }

    public void putAll(TypeVariableMap otherMap) {
        variables.putAll(otherMap.variables);
        types.putAll(otherMap.types);
    }
}
