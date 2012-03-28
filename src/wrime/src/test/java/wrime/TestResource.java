package wrime;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class TestResource {
    private final Class testClass;

    public TestResource(Class testClass) {
        this.testClass = testClass;
    }

    public String relative(String path) {
        return testClass.getSimpleName() + "/" + path;
    }

    public String getResourceBasePath() {
        try {
            return new ClassPathResource(relative("."), testClass).getURL().toString();
        } catch (IOException e) {
            return "";
        }
    }

    public ScriptResource load(String name) {
        String resource = relative(name);
        String resourceBase = getResourceBasePath();
        return loadResource(resource, resourceBase);
    }

    public ScriptResource loadResource(String url, final String resourceBase) {
        return loadResource0(new ClassPathResource(url, testClass), resourceBase);
    }

    public ScriptResource loadResource0(final ClassPathResource resource, final String resourceBase) {
        return new ScriptResource() {
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
                String path = resource.getPath();
                if (resourceBase != null && path.startsWith(resourceBase)) {
                    return path.substring(resourceBase.length());
                }
                return path;
            }

            @Override
            public ScriptResource getResource(String path) throws WrimeException {
                return loadResource0((ClassPathResource) resource.createRelative(path), resourceBase);
            }

            @Override
            public long getLastModified() throws WrimeException {
                try {
                    return resource.lastModified();
                } catch (IOException e) {
                    throw new WrimeException("fail to get last modified time", e);
                }
            }
        };
    }

    public void verify(String expectedResourceName, String content) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(load(expectedResourceName).getInputStream(), writer, "UTF-8");
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (WrimeException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(writer.toString(), content);
    }
}
