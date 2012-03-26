package wrime.bytecode;

import java.util.List;

public class SourceResult {
    private String stderr;
    private List diagnostic;
    private boolean success;

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
}
