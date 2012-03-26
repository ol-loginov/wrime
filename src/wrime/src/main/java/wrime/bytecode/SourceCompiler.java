package wrime.bytecode;

import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

public class SourceCompiler {
    private static final JavaCompiler compiler;
    private static final Charset utf8 = Charset.forName("utf-8");

    static {
        compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
        if (null == compiler) {
            throw new IllegalStateException("no Java compiler available");
        }
    }

    private final File workingFolder;

    public SourceCompiler(File workingFolder) {
        this.workingFolder = workingFolder;
    }

    public void compile(String name, String code, SourceResult result) throws IOException {
        StringWriter stderr = new StringWriter();
        DiagnosticCollector diagnostic = new DiagnosticCollector();

        File sourceFile = writeSourceFile(name, code);
        JavaFileManager fileManager = compiler.getStandardFileManager(diagnostic, null, utf8);
        JavaFileObject sourceObject = fileManager.getJavaFileForInput(StandardLocation.locationFor(sourceFile.getParent()), name, JavaFileObject.Kind.SOURCE);
        JavaCompiler.CompilationTask task = compiler.getTask(stderr, fileManager, diagnostic, null, null, Arrays.asList(sourceObject));
        result.setSuccess(task.call());
        result.setStderr(stderr.toString());
        result.setDiagnostic(diagnostic.getDiagnostics());
        fileManager.close();
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

//        return new SimpleJavaFileObject(URI.create(javaFile.getAbsolutePath()), JavaFileObject.Kind.SOURCE) {
//            @Override
//            public InputStream openInputStream() throws IOException {
//                return new FileInputStream(javaFile);
//            }
//
//            @Override
//            public OutputStream openOutputStream() throws IOException {
//                return new FileOutputStream(javaFile);
//            }
//
//            @Override
//            public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
//                InputStreamReader reader = new InputStreamReader(openInputStream(), utf8);
//                StringWriter bufferContent = new StringWriter();
//                CharBuffer buffer = CharBuffer.allocate(10 * 1024);
//                while (reader.read(buffer) > 0) {
//                    int length = buffer.position();
//                    buffer.rewind();
//                    bufferContent.append(buffer, 0, length);
//                    buffer.rewind();
//                }
//                reader.close();
//                return bufferContent.toString();
//            }
//        };
    }
}
