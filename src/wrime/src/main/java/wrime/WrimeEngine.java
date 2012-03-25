package wrime;

import wrime.tags.*;

import java.io.File;
import java.io.IOException;
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
    private final List<TagFactory> tagFactories;

    private String rootPath;
    private ClassLoader rootLoader;

    private Map<Scanner, String> scannerOptions = new TreeMap<Scanner, String>();
    private Map<Compiler, String> compilerOptions = new TreeMap<Compiler, String>();

    public String getRootPath() {
        return rootPath;
    }

    public WrimeEngine() throws WrimeException {
        createWorkingFolder();

        setOption(Compiler.FUNCTOR_PREFIX, "functor:");

        tagFactories = new ArrayList<TagFactory>() {{
            add(new ParamFactory());
            add(new IncludeFactory());
            add(new ImportFactory());
            add(new ForFactory());
            add(new ContinueFactory());
            add(new BreakFactory());
            add(new IfFactory());
        }};
    }

    private void createWorkingFolder() throws WrimeException {
        File tmpFolder;
        try {
            tmpFolder = File.createTempFile("wrime", "");
        } catch (IOException e) {
            throw new WrimeException("fail to create working folder", e);
        }

        if (!tmpFolder.delete()) {
            throw new WrimeException("fail to delete just created temporary file", null);
        }
        if (!tmpFolder.mkdir()) {
            throw new WrimeException("fail to create directory from just created temporary file", null);
        }
        tmpFolder.deleteOnExit();

        URL tmpFolderUrl;
        try {
            tmpFolderUrl = tmpFolder.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new WrimeException("fail to create working folder URL", e);
        }

        this.rootPath = tmpFolder.getAbsolutePath();
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
