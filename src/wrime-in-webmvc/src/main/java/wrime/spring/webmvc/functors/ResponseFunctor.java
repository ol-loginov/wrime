package wrime.spring.webmvc.functors;

import org.springframework.web.context.request.ServletWebRequest;

@SuppressWarnings("UnusedDeclaration")
public class ResponseFunctor {
    private final ServletWebRequest request;

    public ResponseFunctor() {
        this(null);
    }

    public ResponseFunctor(ServletWebRequest request) {
        this.request = request;
    }

    public void setContentType(String contentType) {
        request.getResponse().setContentType(contentType);
    }
}
