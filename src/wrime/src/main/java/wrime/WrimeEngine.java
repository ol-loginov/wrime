package wrime;

import wrime.bytecode.SourceCompiler;
import wrime.bytecode.SourceResult;
import wrime.config.WrimeConfiguration;
import wrime.output.IncludeWriterListener;
import wrime.output.WrimeWriter;
import wrime.scanner.WrimeScanner;
import wrime.scanner.WrimeScannerImpl;
import wrime.tags.TagFactory;

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
    private final Object COMPILER_LOCK = new Object();

    private final Map<String, WriterRecord> urlToClassMappings = new HashMap<String, WriterRecord>();
    private Map<String, Object> functors = new HashMap<String, Object>();
    private List<TagFactory> tags = new ArrayList<TagFactory>();

    private File rootPath;
    private URLClassLoader rootLoader;

    private Map<Scanner, String> scannerOptions = new TreeMap<Scanner, String>();
    private Map<Compiler, String> compilerOptions = new TreeMap<Compiler, String>();

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
        this.rootPath = workingFolder;
        resetRootLoader();
    }

    private void resetRootLoader() {
        URL tmpFolderUrl;
        try {
            tmpFolderUrl = this.rootPath.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new WrimeException("fail to initialize working folder URL", e);
        }
        this.rootLoader = new URLClassLoader(new URL[]{tmpFolderUrl}, getClass().getClassLoader());
    }

    public void render(ScriptResource resource, Writer out, Map<String, Object> map) throws WrimeException {
        render0(resource, out, map, map);
    }

    public void render(ScriptResource resource, Writer out, Map<String, Object> map, Map<String, Object> previousMap) throws WrimeException {
        render0(resource, out, map, previousMap);
    }

    private String getFunctorPrefix() {
        String prefix = compilerOptions.get(Compiler.FUNCTOR_PREFIX);
        if (prefix == null || prefix.length() == 0) {
            throw new WrimeException("lack of functor prefix option", null);
        }
        return prefix;
    }

    public WrimeEngine addFunctorToModel(Map<String, Object> map, String key, Object value) {
        map.put(getFunctorPrefix() + key, value);
        return this;
    }

    private Map<String, Object> expandFunctorMap(Map<String, Object> copyFrom) {
        TreeMap<String, Object> result = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> functor : getFunctors().entrySet()) {
            String key = getFunctorPrefix() + functor.getKey();
            if (copyFrom != null && copyFrom.containsKey(key)) {
                result.put(key, copyFrom.get(key));
            } else {
                result.put(key, functor.getValue());
            }
        }
        return result;
    }

    private void render0(ScriptResource resource, Writer out, Map<String, Object> map, Map<String, Object> previousMap) throws WrimeException {
        map.putAll(expandFunctorMap(previousMap));
        getRendererClass(resource, out).render(map);
    }

    private WrimeWriter getRendererClass(ScriptResource resource, Writer out) throws WrimeException {
        String path = resource.getPath();
        WriterRecord record;

        synchronized (COMPILER_LOCK) {
            record = urlToClassMappings.get(path);

            // check for expire
            if (record != null && record.lastModified < resource.getLastModified()) {
                resetRootLoader();
                if (!record.sourceFile.delete()) {
                    throw new WrimeException("unable to delete obsolete source file " + record.sourceFile.getAbsolutePath(), null);
                }
                if (!record.classFile.delete()) {
                    throw new WrimeException("unable to delete obsolete class file " + record.classFile.getAbsolutePath(), null);
                }
                record = null;
            }

            if (record == null) {
                record = new WriterRecord();
                record.lastModified = resource.getLastModified();
                record.writerClass = compile(record, parse(resource));
                urlToClassMappings.put(path, record);
            }
        }

        Constructor<? extends WrimeWriter> writerConstructor;
        try {
            writerConstructor = record.writerClass.getConstructor(Writer.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Wrime constructor is na", e);
        }

        WrimeWriter writer;
        try {
            writer = writerConstructor.newInstance(out);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Wrime instance is na", e);
        }
        writer.setIncludeWriterListener(new IncludeWriterListenerImpl(resource));
        return writer;
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

    private Class<WrimeWriter> compile(WriterRecord record, WrimeCompiler code) throws WrimeException {
        SourceCompiler compiler = new SourceCompiler(getRootPath(), getRootLoader());
        SourceResult errors = new SourceResult();
        try {
            record.className = code.getClassName();
            compiler.compile(code.getClassName(), code.getClassCode(), errors);
        } catch (IOException e) {
            throw new WrimeException("writer code is not available", e);
        }

        if (!errors.isSuccess()) {
            throw new WrimeException("writer code is invalid: \n" + errors.getErrorList(), null);
        }

        record.classFile = errors.getClassFile();
        record.sourceFile = errors.getSourceFile();

        try {
            return (Class<WrimeWriter>) getRootLoader().loadClass(code.getClassName());
        } catch (ClassNotFoundException e) {
            throw new WrimeException("writer class is not available", e);
        }
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

    protected ClassLoader getRootLoader() {
        return rootLoader;
    }

    protected File getRootPath() {
        return rootPath;
    }

    public List<TagFactory> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public WrimeEngine setTags(List<TagFactory> tags) {
        this.tags.addAll(tags);
        return this;
    }

    protected Map<String, Object> getFunctors() {
        return Collections.unmodifiableMap(functors);
    }

    public WrimeEngine setFunctors(Map<String, Object> functors) {
        this.functors.putAll(functors);
        return this;
    }

    public enum Scanner {
        EAT_SPACE
    }

    public enum Compiler {
        FUNCTOR_PREFIX
    }

    private class IncludeWriterListenerImpl implements IncludeWriterListener {
        private final ScriptResource parentResource;

        private IncludeWriterListenerImpl(ScriptResource parentResource) {
            this.parentResource = parentResource;
        }

        @Override
        public void include(WrimeWriter caller, String resource, Map<String, Object> model, Writer out) {
            WrimeEngine.this.render0(parentResource.getResource(resource), out, model, caller.getCurrentModel());
        }
    }

    static class WriterRecord {
        Class<? extends WrimeWriter> writerClass;
        long lastModified;
        String className;
        File classFile;
        File sourceFile;
    }
}
