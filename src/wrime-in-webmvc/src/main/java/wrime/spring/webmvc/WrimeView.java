package wrime.spring.webmvc;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.view.AbstractTemplateView;
import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.spring.AbstractScriptSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class WrimeView extends AbstractTemplateView {
    private final static String SERVLET_FUNCTOR = "servlet";

    private static WrimeEngine wrimeEngine;
    private ResourceLoader resourceLoader;

    protected void registerDefaultFunctors() {
        getWrimeEngine().setFunctors(new TreeMap<String, Object>() {{
            put(SERVLET_FUNCTOR, new ServletFunctor());
        }});
    }

    protected void addDefaultFunctors(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        getWrimeEngine().addFunctorToModel(map, SERVLET_FUNCTOR, new ServletFunctor(new ServletWebRequest(request, response)));
    }

    protected WrimeEngine getWrimeEngine() {
        if (wrimeEngine == null) {
            wrimeEngine = new WrimeEngine();
        }
        return wrimeEngine;
    }

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        resourceLoader = context;
        registerDefaultFunctors();
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelMap map = new ModelMap();
        map.addAllAttributes(model);
        addDefaultFunctors(map, request, response);

        Resource resource = resourceLoader.getResource(getUrl());
        ScriptResource script = new AbstractScriptSource((AbstractResource) resource);
        wrimeEngine.render(script, new PrintWriter(response.getOutputStream()), map);
    }
}
