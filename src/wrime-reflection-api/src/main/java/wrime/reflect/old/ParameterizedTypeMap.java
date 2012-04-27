package wrime.reflect.old;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeMap {
    private List<TypeVariable> variables;
    private List<Type> types;

    public ParameterizedTypeMap() {
        variables = new ArrayList<TypeVariable>();
        types = new ArrayList<Type>();
    }

    public int size() {
        return types.size();
    }

    public void add(Type variable, Type typeProxy) {
        if (variable instanceof TypeVariable) {
            variables.add((TypeVariable) variable);
            types.add(typeProxy);
        }
    }

    public TypeVariable getVariable(int index) {
        return variables.get(index);
    }

    public Type getVariableType(int index) {
        return types.get(index);
    }
}
