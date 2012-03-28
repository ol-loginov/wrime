package wrime.bytecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrime.util.EscapeUtils;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class SourceCompiler {
    private static final Logger logger = LoggerFactory.getLogger(SourceCompiler.class);
    private static final Charset utf8 = Charset.forName("utf-8");
    private static final JavaCompiler compiler;

    static {
        compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
        if (null == compiler) {
            throw new IllegalStateException("no Java compiler available");
        }
    }

    private final File workingFolder;
    private final ClassLoader workingLoader;
    private final StandardJavaFileManager fileManager;

    public SourceCompiler(File workingFolder, ClassLoader workingLoader) {
        this.workingFolder = workingFolder;
        this.fileManager = compiler.getStandardFileManager(null, null, utf8);
        this.workingLoader = workingLoader;
    }


    private ClassLoaderInterface getClassLoaderInterface() {
        ClassLoaderInterface classLoaderInterface = null;
//        ServletContext ctx = ServletActionContext.getServletContext();
//        if (ctx != null)
//            classLoaderInterface = (ClassLoaderInterface) ctx.getAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE);
        return EscapeUtils.defaultIfNull(classLoaderInterface, new ClassLoaderInterfaceDelegate(workingLoader));
    }

    public void compile(String name, String code, SourceResult result) throws IOException {
        compile(name, code, result, new TreeSet<String>());
    }

    public void compile(String name, String code, SourceResult result, Set<String> extraClassPath) throws IOException {
        //build classpath
        //some entries will be added multiple times, hence the set
        Set<String> classPath = new HashSet<String>();

        //find available jars
        ClassLoaderInterface classLoaderInterface = getClassLoaderInterface();
        UrlSet urlSet = new UrlSet(classLoaderInterface);

        //find jars
        List<URL> urls = urlSet.getUrls();

        for (URL url : urls) {
            URL normalizedUrl = URLUtil.normalizeToFileProtocol(url);
            File file = FileUtils.toFile((URL) EscapeUtils.defaultIfNull(normalizedUrl, url));
            if (file.exists())
                classPath.add(file.getAbsolutePath());
        }

        //these should be in the list already, but I am feeling paranoid

        //this jar
        //classPath.add(FileUtils.getJarUrl(EmbeddedJSPResult.class));

        //servlet api
        //classPath.add(FileUtils.getJarUrl(Servlet.class));

        //jsp api
        //classPath.add(FileUtils.getJarUrl(JspPage.class));

        //add extra classpath entries (jars where tlds were found will be here)
        classPath.addAll(extraClassPath);

        String workingClassPath = EscapeUtils.join(classPath, File.pathSeparator);
        logger.info("Compiling with classpath [" + workingClassPath + "]");

        List<String> optionList = new ArrayList<String>();
        optionList.add("-g");
        optionList.addAll(Arrays.asList("-source", "1.6"));
        optionList.addAll(Arrays.asList("-target", "1.6"));
        optionList.addAll(Arrays.asList("-classpath", workingClassPath));

        StringWriter errorOutput = new StringWriter();
        DiagnosticCollector<JavaFileObject> diagnostic = new DiagnosticCollector<JavaFileObject>();

        result.setSourceFile(writeSourceFile(name, code));
        result.setClassFile(new File(result.getSourceFile().getParentFile(), name + ".class"));

        JavaCompiler.CompilationTask task = compiler.getTask(errorOutput, fileManager, diagnostic, optionList, null,
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(result.getSourceFile())));

        result.setSuccess(task.call());
        result.setStderr(errorOutput.toString());
        result.setDiagnostic(diagnostic.getDiagnostics());
    }

    private File writeSourceFile(String name, String code) throws IOException {
        final File javaFile = new File(workingFolder, name + ".java");
        OutputStreamWriter fileWriter = null;
        try {
            fileWriter = new OutputStreamWriter(new FileOutputStream(javaFile), utf8);
            fileWriter.write(code);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
        return javaFile;
    }

    private static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
