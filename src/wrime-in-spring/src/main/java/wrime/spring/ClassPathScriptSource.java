package wrime.spring;

import org.springframework.core.io.ClassPathResource;
import wrime.ScriptResource;
import wrime.WrimeException;

import java.io.IOException;
import java.io.InputStream;

public class ClassPathScriptSource implements ScriptResource {
    private final ClassPathResource resource;

    public ClassPathScriptSource(ClassPathResource resource) {
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
        return resource.getPath();
    }
}
