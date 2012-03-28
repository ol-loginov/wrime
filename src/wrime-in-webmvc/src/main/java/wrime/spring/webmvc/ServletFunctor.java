package wrime.spring.webmvc;

import org.springframework.web.context.request.ServletWebRequest;

public class ServletFunctor {
    private final ServletWebRequest request;

    public ServletFunctor() {
        this(null);
    }

    public ServletFunctor(ServletWebRequest request) {
        this.request = request;
    }

    public void setContentType(String contentType) {
        request.getResponse().setContentType(contentType);
    }
}
