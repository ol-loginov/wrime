package wrime.ast;

import wrime.WrimeException;
import wrime.util.EscapeUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class EmitterWriter {
    private final Writer writer;

    public EmitterWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(Emitter emitter) throws IOException {
        if (emitter == null) {
            throw new IllegalArgumentException("emitter should not be null");
        }

        LineHolder line = new LineHolder(emitter);
        while (!line.isEmpty()) {
            Object item = line.take();
            if (item == null) {
                throw new IllegalStateException("One of emitters becomes null!");
            }

            if (item instanceof Emitter) {
                line.put(toJavaWords((Emitter) item));
            } else {
                writer.append(item.toString());
            }
        }
    }

    private List<Object> asList(Object... objects) {
        return Arrays.asList(objects);
    }

    private List<Object> toJavaWords(Emitter emitter) throws IOException {
        if (emitter instanceof Gate) {
            return toJavaWords0((Gate) emitter);
        } else if (emitter instanceof NumberValue) {
            return toJavaWords0((NumberValue) emitter);
        } else if (emitter instanceof BoolValue) {
            return toJavaWords0((BoolValue) emitter);
        } else if (emitter instanceof NullValue) {
            return toJavaWords0();
        } else if (emitter instanceof Group) {
            return toJavaWords0((Group) emitter);
        } else if (emitter instanceof Inverter) {
            return toJavaWords0((Inverter) emitter);
        } else if (emitter instanceof Comparison) {
            return toJavaWords0((Comparison) emitter);
        } else if (emitter instanceof Algebraic) {
            return toJavaWords0((Algebraic) emitter);
        } else if (emitter instanceof StringValue) {
            return toJavaWords0((StringValue) emitter);
        } else if (emitter instanceof MethodCall) {
            return toJavaWords0((MethodCall) emitter);
        } else if (emitter instanceof FunctorRef) {
            return toJavaWords0((FunctorRef) emitter);
        } else if (emitter instanceof VariableRef) {
            return toJavaWords0((VariableRef) emitter);
        } else {
            throw new WrimeException("No way to write emitter of type " + emitter.getClass(), null);
        }
    }

    private List<Object> toJavaWords0(VariableRef emitter) {
        List<Object> result = new ArrayList<Object>();
        result.add(emitter.getName());
        return result;
    }

    private List<Object> toJavaWords0(FunctorRef emitter) {
        List<Object> result = new ArrayList<Object>();
        result.add("this.$" + emitter.getName());
        return result;
    }

    private List<Object> toJavaWords0(MethodCall func) {
        List<Object> result = new ArrayList<Object>();
        result.add(func.getInvocable());
        result.add("." + func.getInvocationName());
        result.add("(");
        if (func.getArguments() != null) {
            boolean first = true;
            for (Emitter arg : func.getArguments()) {
                if (!first) {
                    result.add(", ");
                }
                result.add(arg);
                first = false;
            }
        }
        result.add(")");
        return result;
    }

    private List<Object> toJavaWords0(StringValue emitter) {
        return asList('"' + EscapeUtils.escapeJavaString(emitter.getValue()) + '"');
    }

    private List<Object> toJavaWords0() {
        return asList("null");
    }

    private List<Object> toJavaWords0(BoolValue emitter) {
        return asList(emitter.getValue() ? "true" : "false");
    }

    private List<Object> toJavaWords0(Algebraic c) {
        return asList(c.getLeft(), " " + c.getRule().getJavaSymbol() + " ", c.getRight());
    }

    private List<Object> toJavaWords0(Comparison c) {
        return asList("$c$" + c.getRule(), "(", c.getLeft(), ", ", c.getRight(), ")");
    }

    private List<Object> toJavaWords0(Inverter emitter) {
        return asList("!", emitter.getInner());
    }

    private List<Object> toJavaWords0(Group emitter) {
        return asList("(", emitter.getInner(), ")");
    }

    private List<Object> toJavaWords0(NumberValue emitter) throws IOException {
        return asList(emitter.getText());
    }

    private List<Object> toJavaWords0(Gate gate) throws IOException {
        return asList(gate.getLeft(), " " + gate.getRule().getJavaSymbol() + " ", gate.getRight());
    }

    static class LineHolder {
        private Deque<Object> line = new LinkedList<Object>();

        public LineHolder(Emitter emitter) {
            line.add(emitter);
        }

        public boolean isEmpty() {
            return line.isEmpty();
        }

        public Object take() {
            return line.pollFirst();
        }

        public void put(Object... values) {
            put(Arrays.asList(values));
        }

        /**
         * danger! reverse input list
         *
         * @param list to be inserted
         */
        private void put(List<Object> list) {
            Collections.reverse(list);
            for (Object o : list) {
                line.addFirst(o);
            }
        }
    }
}
