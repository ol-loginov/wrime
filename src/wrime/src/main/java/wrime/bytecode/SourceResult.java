package wrime.bytecode;

import java.io.File;
import java.util.List;

public class SourceResult {
    private String stderr;
    private List diagnostic;
    private boolean success;

    private File sourceFile;
    private File classFile;

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public List getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(List diagnostic) {
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
}
