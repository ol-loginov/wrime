package wrime.spring.webmvc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
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
import wrime.spring.webmvc.functors.L18nFunctor;
import wrime.spring.webmvc.functors.ResponseFunctor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("UnusedDeclaration")
public class WrimeView extends AbstractTemplateView implements MessageSourceAware {
    private final static String RESPONSE_FUNCTOR = "response";
    private final static String MESSAGE_FUNCTOR = "l18n";

    private ResourceLoader resourceLoader;
    private WrimeEngine wrimeEngine;
    private MessageSource messageSource;

    protected void registerWebRequestFunctors() {
        getWrimeEngine().setFunctors(new TreeMap<String, Object>() {{
            put(RESPONSE_FUNCTOR, new ResponseFunctor());
            put(MESSAGE_FUNCTOR, new L18nFunctor());
        }});
    }

    protected void setupWebRequestFunctors(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        getWrimeEngine().addFunctorToModel(map, RESPONSE_FUNCTOR, new ResponseFunctor(new ServletWebRequest(request, response)));
        getWrimeEngine().addFunctorToModel(map, MESSAGE_FUNCTOR, new L18nFunctor(messageSource, request.getLocale()));
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;

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
                return getApplicationContext().getBean("wrimeViewEngine", WrimeEngineFactory.class).create();
            } else {
                return getApplicationContext().getBean(names.get(0), WrimeEngineFactory.class).create();
            }
        }
        throw new ApplicationContextException("Must define a WrimeEngineFactory or WrimeEngineFactoryAdapter bean in this web application context. Use default name 'wrimeViewEngine' if necessary");
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelMap map = new ModelMap();
        map.addAllAttributes(model);
        setupWebRequestFunctors(map, request, response);

        response.setCharacterEncoding(WrimeEngine.UTF_8.name());

        Resource resource = resourceLoader.getResource(getUrl());
        ScriptResource script = new AbstractScriptSource((AbstractResource) resource);
        wrimeEngine.render(script, response.getWriter(), map);
    }
}
