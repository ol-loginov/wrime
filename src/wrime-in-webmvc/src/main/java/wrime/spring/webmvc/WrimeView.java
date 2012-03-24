package wrime.spring.webmvc;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.AbstractTemplateView;
import wrime.ScriptResource;
import wrime.WrimeEngine;
import wrime.spring.AnonymousScriptSource;
import wrime.spring.ClassPathScriptSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;


@SuppressWarnings("UnusedDeclaration") // because it's used outside the module
public class WrimeView extends AbstractTemplateView {
    private WrimeEngine wrimeEngine;
    private ResourceLoader resourceLoader;

    public WrimeEngine getWrimeEngine() {
        return wrimeEngine;
    }

    public void setWrimeEngine(WrimeEngine wrimeEngine) {
        this.wrimeEngine = wrimeEngine;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelMap map = new ModelMap();
        map.addAllAttributes(model);
        map.put("request", request);
        map.put("response", response);

        Resource resource = resourceLoader.getResource(getUrl());

        ScriptResource script;
        if (resource instanceof ClassPathResource) {
            script = new ClassPathScriptSource((ClassPathResource) resource);
        } else {
            script = new AnonymousScriptSource(resource);
        }

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        wrimeEngine
                .newWriter(script, writer)
                .render(map);
    }
}
