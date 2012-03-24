package wrime;

import java.io.InputStream;

public interface ScriptResource {
    InputStream getInputStream() throws WrimeException;

    String getPath();
}
