package wrime.tags;

import wrime.*;
import wrime.ops.Chain;
import wrime.ops.Operand;
import wrime.ops.Raw;

import java.util.ArrayList;
import java.util.List;

public class IncludeReceiver extends PathReceiver {
    enum Status {
        WAIT_START,
        WAIT_PATH,
        WAIT_PARAMETER,
        COMPLETE
    }

    static class TemplateParameter {
        String name;
        Operand getter;
    }

    private Status status = Status.WAIT_START;

    private Operand templatePath;
    private List<TemplateParameter> templateModel = new ArrayList<TemplateParameter>();

    public void errorIncomplete(String message) throws WrimeException {
        super.error("incomplete ${include(...)} expression, " + message);
    }

    @Override
    public void beginList(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case WAIT_START:
                storeTransientParameters(scope);
                waitForPath(scope);
                break;
            default:
                errorUnexpected(WrimeScanner.OPEN_LIST_SYMBOL);
        }
    }

    private void storeTransientParameters(ExpressionContextKeeper scope) {
        for (ParameterName parameter : scope.getModelParameters()) {
            if (parameter.getOption().contains("transient")) {
                TemplateParameter model = new TemplateParameter();
                model.name = parameter.getName();
                model.getter = new Raw("this." + parameter.getName());
                templateModel.add(model);
            }
        }
    }

    private void waitForPath(ExpressionContextKeeper scope) throws WrimeException {
        path.push(new CallReceiver().setCloser(createPathCloser()), scope);
    }

    private CompleteCallback createPathCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                status = last ? Status.COMPLETE : Status.WAIT_PARAMETER;
                templatePath = ((CallReceiver) child).getOperand();
                if (!last) {
                    waitForParameter(scope);
                }
            }
        };
    }

    private void waitForParameter(ExpressionContextKeeper scope) throws WrimeException {
        path.push(new AssignReceiver().setCompleteCallback(createParameterCloser()), scope);
    }

    private CompleteCallback createParameterCloser() {
        return new CompleteCallback() {
            @Override
            public void complete(PathReceiver child, ExpressionContextKeeper scope, boolean last) throws WrimeException {
                path.remove(child);
                status = last ? Status.COMPLETE : Status.WAIT_PARAMETER;

                TemplateParameter parameter = new TemplateParameter();
                parameter.name = ((AssignReceiver) child).getAlias();
                parameter.getter = ((AssignReceiver) child).getSource();
                templateModel.add(parameter);

                if (!last) {
                    waitForParameter(scope);
                } else {
                }
            }
        };
    }

    @Override
    public String getHumanName() {
        return "Template invoker";
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case COMPLETE:
                Chain chain = new Chain();

                String model;
                if (templateModel.size() > 0) {
                    model = String.format("$includeAt_%d_%d", path.getLine(), path.getColumn());
                    chain.getOperands().add(new Raw(String.format("ModelMap %s = new ModelMap();\n", model)));
                    for (TemplateParameter parameter : templateModel) {
                        chain.getOperands().add(new Raw(String.format("%s.put(\"%s\", ", model, EscapeUtils.escapeJavaString(parameter.name))));
                        chain.getOperands().add(parameter.getter);
                        chain.getOperands().add(new Raw(");\n"));
                    }
                } else {
                    model = "null";
                }

                chain.getOperands().add(new Raw(String.format("this.include(")));
                chain.getOperands().add(templatePath);
                chain.getOperands().add(new Raw(String.format(", %s);", model)));
                path.render(chain);
                break;
            default:
                errorIncomplete("waiting for more parameters");
        }
    }
}
