package wrime.tags;

import wrime.ast.TagParam;
import wrime.util.ExpressionContextRoot;

import java.io.IOException;
import java.io.StringWriter;

public class ParamTagProcessor implements TagProcessor {
    private final TagParam tag;

    public ParamTagProcessor(TagParam tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextRoot scope, StringWriter body) throws IOException {
        String option = "";
        if (tag.getOptions().size() > 0) {
            option = tag.getOptions().get(0).getText();
        }
        scope.addModelParameter(
                tag.getClassName().toString(),
                tag.getParamName().getText(),
                scope.findClass(tag.getClassName()),
                option);
    }
}
