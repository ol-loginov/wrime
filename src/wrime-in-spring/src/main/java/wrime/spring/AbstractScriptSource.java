package wrime.spring;

import org.springframework.core.io.AbstractResource;
import wrime.ScriptResource;
import wrime.WrimeException;

import java.io.IOException;
import java.io.InputStream;

public class AbstractScriptSource implements ScriptResource {
    private final AbstractResource resource;

    public AbstractScriptSource(AbstractResource resource) {
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

    @Override
    public ScriptResource getResource(String path) throws WrimeException {
        try {
            return new AbstractScriptSource((AbstractResource) resource.createRelative(path));
        } catch (IOException e) {
            throw new WrimeException("unable to get resource " + path, null);
        }
    }

    @Override
    public long getLastModified() throws WrimeException {
        try {
            return resource.lastModified();
        } catch (IOException e) {
            throw new WrimeException("unable to get last modified of " + getPath(), null);
        }
    }
}
