package wrime.spring.webmvc;

import org.springframework.beans.factory.FactoryBean;
import wrime.WrimeEngine;
import wrime.WrimeEngineFactory;

import java.util.HashMap;

public class WrimeEngineFactoryAdapter extends WrimeEngineFactory implements FactoryBean<WrimeEngine> {
    private WrimeEngine engine;

    @Override
    public Class<WrimeEngine> getObjectType() {
        return WrimeEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public WrimeEngine getObject() {
        if (engine == null) {
            engine = create();
        }
        return engine;
    }

    @Override
    public void initializeFunctors(WrimeEngine engine) {
        engine.setFunctors(new HashMap<String, Object>() {{
            put("servlet", new ServletFunctor());
        }});
    }
}
