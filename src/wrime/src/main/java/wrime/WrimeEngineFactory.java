package wrime;

import wrime.functor.LogicFunctor;
import wrime.functor.MathFunctor;
import wrime.functor.ObjectFunctor;
import wrime.functor.StringFunctor;
import wrime.tags.*;

import java.io.File;
import java.io.IOException;
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
        engine
                .registerFunctor("o", ObjectFunctor.class, new ObjectFunctor())
                .registerFunctor("s", StringFunctor.class, new StringFunctor())
                .registerFunctor("l", LogicFunctor.class, new LogicFunctor())
                .registerFunctor("m", MathFunctor.class, new MathFunctor());
    }

    public void initializeTags(WrimeEngine engine) {
        engine.setTags(new TreeMap<String, TagFactory>() {{
            put("param", new ParamTagFactory());
            put("include", new IncludeTagFactory());
            put("import", new ImportTagFactory());
            put("for", new ForTagFactory());
            put("continue", new ContinueTagFactory());
            put("break", new BreakTagFactory());
            put("param", new ParamTagFactory());
            put("if", new IfTagFactory());
            put("set", new SetTagFactory());
        }});
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
