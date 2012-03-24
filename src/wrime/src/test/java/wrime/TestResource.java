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

    public ScriptResource load(String url) {
        final String resourceBase = getResourceBasePath();
        final ClassPathResource resource = new ClassPathResource(relative(url), testClass);
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
