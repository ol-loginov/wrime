package wrime.spring;

import org.springframework.core.io.Resource;
import wrime.ScriptResource;
import wrime.WrimeException;

import java.io.IOException;
import java.io.InputStream;

public class AnonymousScriptSource implements ScriptResource {
    private final Resource resource;

    public AnonymousScriptSource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public InputStream getInputStream() throws WrimeException {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new WrimeException("fail to open resource", e);
        }
    }

    @Override
    public String getPath() {
        try {
            return resource.getURL().toString();
        } catch (IOException e) {
            return resource.getFilename();
        }
    }
}
