package wrime;

import wrime.bytecode.SourceCompiler;
import wrime.bytecode.SourceComposer;
import wrime.bytecode.SourceResult;
import wrime.output.IncludeCallback;
import wrime.output.WrimeWriter;
import wrime.scanner.WrimeScanner;
import wrime.scanner.WrimeScannerImpl;
import wrime.tags.TagFactory;
import wrime.util.EscapeUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WrimeEngine {
    public static final Charset UTF_8 = Charset.forName("utf-8");

    private final Object COMPILER_LOCK;

    private final Map<String, WriterRecord> urlToClassMappings;
    private Map<String, FunctorClass> functors;
    private Map<String, TagFactory> tags;

    private File rootPath;
    private URLClassLoader rootLoader;

    private Map<Scanner, String> scannerOptions;
    private Map<Compiler, String> compilerOptions;

    WrimeEngine() {
        COMPILER_LOCK = new Object();
        urlToClassMappings = new HashMap<String, WriterRecord>();
        functors = new HashMap<String, FunctorClass>();
        tags = new TreeMap<String, TagFactory>();
        scannerOptions = new TreeMap<Scanner, String>();
        compilerOptions = new TreeMap<Compiler, String>();
    }

    void setWorkingFolder(File workingFolder) throws WrimeException {
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

    private Map<String, Object> createFunctorMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, FunctorClass> functor : functors.entrySet()) {
            result.put(getFunctorPrefix() + functor.getKey(), functor.getValue().getFunctorInstance());
        }
        return result;
    }

    public void render(ScriptResource resource, Writer out, Map<String, Object> map) throws WrimeException {
        Map<String, Object> renderMap = createFunctorMap();
        renderMap.putAll(EscapeUtils.defaultIfNull(map, new TreeMap<String, Object>()));
        render0(resource, out, renderMap);
    }

    private void render0(ScriptResource resource, Writer out, Map<String, Object> map) throws WrimeException {
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
                    if (record.sourceFile.exists()) {
                        throw new WrimeException("unable to delete obsolete source file " + record.sourceFile.getAbsolutePath(), null);
                    }
                }
                if (!record.classFile.delete()) {
                    if (record.classFile.exists()) {
                        throw new WrimeException("unable to delete obsolete class file " + record.classFile.getAbsolutePath(), null);
                    }
                }
                record = null;
            }

            if (record == null) {
                record = new WriterRecord();
                record.lastModified = resource.getLastModified();
                record.writerClass = compile(record, compose(resource));
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
        writer.setIncludeCallback(new IncludeCallbackImpl(resource));
        return writer;
    }

    protected void scan(ScriptResource resource, WrimeScanner.Receiver receiver) throws WrimeException {
        WrimeScanner scanner = new WrimeScannerImpl();
        scanner.configure(scannerOptions);
        scanner.scan(resource, receiver);
    }

    protected SourceComposer compose(ScriptResource resource) throws WrimeException {
        SourceComposer compiler = new SourceComposer(getRootLoader(), functors, getTags());
        compiler.configure(compilerOptions);
        scan(resource, compiler.createReceiver());
        return compiler;
    }

    private Class<WrimeWriter> compile(WriterRecord record, SourceComposer code) throws WrimeException {
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

    public Map<String, TagFactory> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    public WrimeEngine setTags(Map<String, TagFactory> tags) {
        this.tags.putAll(tags);
        return this;
    }

    /*
    protected Map<String, FunctorClass> getFunctors() {
        return Collections.unmodifiableMap(functors);
    }     */


    public <T> WrimeEngine registerFunctor(String functorName, Class<T> functorClass, T defaultInstance) {
        FunctorClass rec = new FunctorClass();
        rec.setFunctorId(functorName);
        rec.setFunctorType(functorClass);
        rec.setFunctorInstance(defaultInstance);
        functors.put(functorName, rec);
        return this;
    }

    public enum Scanner {
        EAT_SPACE
    }

    public enum Compiler {
        FUNCTOR_PREFIX
    }

    private class IncludeCallbackImpl implements IncludeCallback {
        private final ScriptResource parentResource;

        private IncludeCallbackImpl(ScriptResource parentResource) {
            this.parentResource = parentResource;
        }

        @Override
        public void include(WrimeWriter caller, String resource, Map<String, Object> model, Writer out) {
            WrimeEngine.this.render0(parentResource.getResource(resource), out, model);
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
