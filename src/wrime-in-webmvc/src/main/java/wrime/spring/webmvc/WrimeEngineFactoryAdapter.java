package wrime.spring.webmvc;

import wrime.WrimeEngine;
import wrime.WrimeEngineFactory;

import java.util.HashMap;

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
        engine.setFunctors(new HashMap<String, Object>() {{
            put("servlet", new ServletFunctor());
        }});
    }
}
