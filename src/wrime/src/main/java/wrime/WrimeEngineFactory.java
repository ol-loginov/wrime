package wrime;

import wrime.functor.LogicFunctor;
import wrime.functor.MathFunctor;
import wrime.functor.StringFunctor;
import wrime.tags.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;

public class WrimeEngineFactory {
    public WrimeEngine create() {
        WrimeEngine engine = new WrimeEngine();
        engine.setWorkingFolder(createWorkingFolder(engine));
        initializeOptions(engine);
        initializeTags(engine);
        initializeFunctors(engine);
        return engine;
    }

    public void initializeFunctors(WrimeEngine engine) {
        engine.setFunctors(new TreeMap<String, Object>() {{
            put("str", new StringFunctor());
            put("logic", new LogicFunctor());
            put("math", new MathFunctor());
        }});
    }

    public void initializeTags(WrimeEngine engine) {
        engine.setTags(Arrays.asList(
                new ParamFactory(),
                new IncludeFactory(),
                new ImportFactory(),
                new ForFactory(),
                new ContinueFactory(),
                new BreakFactory(),
                new IfFactory(),
                new SetFactory()
        ));
    }

    public void initializeOptions(WrimeEngine engine) {
        engine.setOption(WrimeEngine.Compiler.FUNCTOR_PREFIX, "functor:");
        engine.setOption(WrimeEngine.Scanner.EAT_SPACE, true);
    }

    public File createWorkingFolder(WrimeEngine engine) {
        File workingFolder;
        try {
            workingFolder = File.createTempFile("wrime", "");
        } catch (IOException e) {
            throw new WrimeException("fail to create working folder", e);
        }
        if (!workingFolder.delete()) {
            throw new WrimeException("fail to delete just created temporary file", null);
        }
        if (!workingFolder.mkdir()) {
            throw new WrimeException("fail to create directory from just created temporary file", null);
        }
        workingFolder.deleteOnExit();
        return workingFolder;
    }
}
