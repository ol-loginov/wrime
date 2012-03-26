package wrime.config;

import wrime.WrimeEngine;
import wrime.WrimeException;
import wrime.functor.LogicFunctor;
import wrime.functor.MathFunctor;
import wrime.functor.StringFunctor;
import wrime.tags.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class WrimeConfiguration {
    public File getWorkingFolder() throws WrimeException {
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

    public void setOptions(WrimeEngine wrime) {
        wrime.setOption(WrimeEngine.Compiler.FUNCTOR_PREFIX, "functor:");
    }

    public void setTagFactories(WrimeEngine engine) {
        engine.getTags().addAll(Arrays.asList(
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

    public void setFunctors(WrimeEngine engine) {
        engine.addFunctor("str", new StringFunctor());
        engine.addFunctor("logic", new LogicFunctor());
        engine.addFunctor("math", new MathFunctor());
    }
}
