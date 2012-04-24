package wrime.spring.webmvc;

import wrime.WrimeEngine;
import wrime.WrimeEngineFactory;

@SuppressWarnings("UnusedDeclaration")
public class WrimeEngineFactoryAdapter extends WrimeEngineFactory {
    private WrimeEngine engine;

    @Override
    public WrimeEngine create() {
        if (engine == null) {
            engine = super.create();
        }
        return engine;
    }

    @Override
    public void initializeFunctors(WrimeEngine engine) {
        super.initializeFunctors(engine);
    }
}
