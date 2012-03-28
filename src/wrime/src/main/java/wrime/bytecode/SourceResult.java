package wrime.bytecode;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.util.List;

public class SourceResult {
    private String stderr;
    private List<Diagnostic<? extends JavaFileObject>> diagnostic;
    private boolean success;

    private File sourceFile;
    private File classFile;

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public void setDiagnostic(List<Diagnostic<? extends JavaFileObject>> diagnostic) {
        this.diagnostic = diagnostic;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public File getClassFile() {
        return classFile;
    }

    public void setClassFile(File classFile) {
        this.classFile = classFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getErrorList() {
        StringBuilder builder = new StringBuilder();
        if (diagnostic != null && diagnostic.size() > 0) {
            for (Diagnostic<? extends JavaFileObject> d : diagnostic) {
                builder.append(d.toString()).append("\n");
            }
        }
        if (stderr != null) {
            builder.append(stderr).append("\n");
        }
        return builder.toString();
    }
}
