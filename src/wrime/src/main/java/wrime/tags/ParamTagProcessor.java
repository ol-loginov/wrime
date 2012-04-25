package wrime.tags;

import wrime.ast.TagParam;
import wrime.output.BodyWriter;
import wrime.util.ExpressionContextKeeper;

import java.io.IOException;

public class ParamTagProcessor implements TagProcessor {
    private final TagParam tag;

    public ParamTagProcessor(TagParam tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionContextKeeper context, BodyWriter body) throws IOException {
        String option = "";
        if (tag.getOptions().size() > 0) {
            option = tag.getOptions().get(0).getText();
        }
        Class paramClass = context.findClass(tag.getClassName());
        context.addParameter(tag.getParamName().getText(), paramClass, option);
    }
}
