package wrime.tags;

import wrime.WrimeException;
import wrime.util.ExpressionContextKeeper;

public class ParamTagProcessor extends PathReceiver implements TagProcessor {
    public static enum Status {
        EXPECT_NAME,
        EXPECT_OPTION
    }

    private String paramType = "";
    private String paramName = "";
    private String paramOption = "";

    private Status status = Status.EXPECT_NAME;

    @Override
    public void error(String message) throws WrimeException {
        super.error("incomplete ${param ...} expression, " + message);
    }

    @Override
    public void complete(ExpressionContextKeeper scope) throws WrimeException {
        switch (status) {
            case EXPECT_NAME:
                error("incomplete parameter definition");
                break;
            case EXPECT_OPTION:
                scope.addModelParameter(paramType, paramName, scope.findClass(paramType), paramOption);
                break;
            default:
                error("incomplete statement");
        }
    }

    @Override
    public void pushDelimiter(ExpressionContextKeeper scope, String delimiter) throws WrimeException {
        if (!".".equals(delimiter) || status != Status.EXPECT_NAME) {
            errorUnexpected(delimiter);
        }
        paramType += ".";
    }

    @Override
    public void pushToken(ExpressionContextKeeper scope, String name) throws WrimeException {
        switch (status) {
            case EXPECT_NAME:
                // situation if we received "." before
                if (paramType.endsWith(".")) {
                    paramType += name;
                }
                // this is definitely not after "."
                else {
                    // we check for paramType. if it's is empty, then this is class name
                    if (paramType.length() == 0) {
                        paramType = name;
                    }
                    // otherwise we check for existence this type. and set parameter name
                    else {
                        if (scope.findClass(paramType) == null) {
                            error("invalid class name " + paramType);
                        }
                        status = Status.EXPECT_NAME;
                        paramName = name;
                        status = Status.EXPECT_OPTION;
                    }
                }
                break;
            case EXPECT_OPTION:
                if (paramOption.length() > 0) {
                    paramOption += " ";
                }
                paramOption += name;
                break;
            default:
                error("invalid syntax");
        }
    }
}
