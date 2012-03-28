package wrime.spring.webmvc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.view.AbstractTemplateView;
import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.WrimeEngineFactory;
import wrime.spring.AbstractScriptSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class WrimeView extends AbstractTemplateView {
    private final static String SERVLET_FUNCTOR = "servlet";

    private ResourceLoader resourceLoader;
    private WrimeEngine wrimeEngine;

    protected void registerWebRequestFunctors() {
        getWrimeEngine().setFunctors(new TreeMap<String, Object>() {{
            put(SERVLET_FUNCTOR, new ServletFunctor());
        }});
    }

    protected void addWebRequestFunctors(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        getWrimeEngine().addFunctorToModel(map, SERVLET_FUNCTOR, new ServletFunctor(new ServletWebRequest(request, response)));
    }

    protected WrimeEngine getWrimeEngine() {
        return wrimeEngine;
    }

    public void setWrimeEngine(WrimeEngine wrimeEngine) {
        this.wrimeEngine = wrimeEngine;
    }

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        resourceLoader = context;

        if (getWrimeEngine() == null) {
            // No explicit VelocityEngine: try to autodetect one.
            setWrimeEngine(autodetectEngine());
        }

        registerWebRequestFunctors();
    }

    /**
     * Autodetect a engine  via the ApplicationContext.
     * Called if no explicit engine has been specified.
     *
     * @return the VelocityEngine to use for VelocityViews
     * @throws org.springframework.beans.BeansException
     *          if no engine could be found
     * @see #getApplicationContext
     */
    protected WrimeEngine autodetectEngine() throws BeansException {
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getApplicationContext(), WrimeEngineFactory.class, true, false)));
        if (names.size() > 0) {
            if (names.contains("wrimeViewEngine")) {
                return getApplicationContext().getBean("wrimeViewEngine", WrimeEngine.class);
            } else {
                return getApplicationContext().getBean(names.get(0), WrimeEngine.class);
            }
        }
        throw new ApplicationContextException("Must define a single WrimeEngineFactory bean in this web application context (may be inherited): WrimeEngineFactory is the usual implementation. This bean may be given any name.");
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelMap map = new ModelMap();
        map.addAllAttributes(model);
        addWebRequestFunctors(map, request, response);

        response.setCharacterEncoding(WrimeEngine.UTF_8.name());

        Resource resource = resourceLoader.getResource(getUrl());
        ScriptResource script = new AbstractScriptSource((AbstractResource) resource);
        wrimeEngine.render(script, response.getWriter(), map);
    }
}
