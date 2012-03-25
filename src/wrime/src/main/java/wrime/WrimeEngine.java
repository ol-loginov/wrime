package wrime;

import wrime.tags.TagFactory;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class WrimeEngine {
    private final Map<String, Class<? extends WrimeWriter>> urlToClassMappings = new HashMap<String, Class<? extends WrimeWriter>>();
    private final Map<String, Object> nameToFunctorMappings = new HashMap<String, Object>();
    private final List<TagFactory> tagFactories = new ArrayList<TagFactory>();

    private String rootPath;
    private ClassLoader rootLoader;

    private Map<Scanner, String> scannerOptions = new TreeMap<Scanner, String>();
    private Map<Compiler, String> compilerOptions = new TreeMap<Compiler, String>();

    public String getRootPath() {
        return rootPath;
    }

    public WrimeEngine() throws WrimeException {
        this(new WrimeConfiguration());
    }

    public WrimeEngine(WrimeConfiguration configuration) throws WrimeException {
        createWorkingFolder(configuration.getWorkingFolder());
        configuration.setOptions(this);
        configuration.setTagFactories(this);
        configuration.setFunctors(this);
    }

    private void createWorkingFolder(File workingFolder) throws WrimeException {
        URL tmpFolderUrl;
        try {
            tmpFolderUrl = workingFolder.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new WrimeException("fail to create working folder URL", e);
        }

        this.rootPath = workingFolder.getAbsolutePath();
        this.rootLoader = new URLClassLoader(new URL[]{tmpFolderUrl}, getClass().getClassLoader());
    }

    public WrimeWriter newWriter(ScriptResource resource, Writer writer) throws Exception {
        String path = resource.getPath();
        Class<? extends WrimeWriter> writerClass = urlToClassMappings.get(path);
        if (writerClass == null) {
            writerClass = compile(parse(resource));
            urlToClassMappings.put(path, writerClass);
        }

        Constructor<? extends WrimeWriter> writerConstructor;
        try {
            writerConstructor = writerClass.getConstructor(Writer.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Wrime constructor is na", e);
        }

        try {
            return writerConstructor.newInstance(writer);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        }
    }

    protected void scan(ScriptResource resource, WrimeScanner.Receiver receiver) throws WrimeException {
        WrimeScanner scanner = new WrimeScannerImpl();
        scanner.configure(scannerOptions);
        scanner.parse(resource, receiver);
    }

    protected WrimeCompiler parse(ScriptResource resource) throws WrimeException {
        WrimeCompiler compiler = new WrimeCompiler(this);
        compiler.configure(compilerOptions);
        scan(resource, compiler.createReceiver());
        return compiler;
    }

    private Class<WrimeWriter> compile(WrimeCompiler parse) {
        return null;
    }

    public WrimeEngine setOption(Scanner option, boolean enable) {
        setOptionInMap(scannerOptions, option, enable ? "" : null);
        return this;
    }

    public WrimeEngine setOption(Compiler option, String value) {
        setOptionInMap(compilerOptions, option, value);
        return this;
    }

    private <T> void setOptionInMap(Map<T, String> optionMap, T option, String value) {
        if (value == null) {
            optionMap.remove(option);
        } else {
            optionMap.put(option, value);
        }
    }

    public ClassLoader getRootLoader() {
        return rootLoader;
    }

    public Collection<TagFactory> getTags() {
        return tagFactories;
    }

    public Iterable<Map.Entry<String, Object>> getFunctors() {
        return nameToFunctorMappings.entrySet();
    }

    public WrimeEngine addFunctor(String name, Object functor) {
        nameToFunctorMappings.put(name, functor);
        return this;
    }

    public enum Scanner {
        EAT_SPACE
    }

    public enum Compiler {
        FUNCTOR_PREFIX
    }
}
