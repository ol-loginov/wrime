package wrime.ast;

public class TagImport extends WrimeTag {
    private final String packagePath;
    private final LocatableString packageTarget;

    public TagImport(String packagePath, LocatableString packageTarget) {
        super("import");
        this.packagePath = packagePath;
        this.packageTarget = packageTarget;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public LocatableString getPackageTarget() {
        return packageTarget;
    }

    public boolean isAllFromPackage() {
        return packageTarget == null;
    }

    public String getJavaImport() {
        return getPackagePath() + (isAllFromPackage() ? "*" : getPackageTarget().getText());
    }
}
