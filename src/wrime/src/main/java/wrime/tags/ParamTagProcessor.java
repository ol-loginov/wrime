package wrime.tags;

import wrime.ast.TagParam;
import wrime.bytecode.ExpressionStack;
import wrime.output.BodyWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class ParamTagProcessor implements TagProcessor {
    private final TagParam tag;

    public ParamTagProcessor(TagParam tag) {
        this.tag = tag;
    }

    @Override
    public void render(ExpressionStack context, BodyWriter body) throws IOException {
        String option = "";
        if (tag.getOptions().size() > 0) {
            option = tag.getOptions().get(0).getText();
        }
        Type paramClass = context.findClass(tag.getClassName());
        context.addParameter(tag.getParamName().getText(), paramClass, option);
    }
}
