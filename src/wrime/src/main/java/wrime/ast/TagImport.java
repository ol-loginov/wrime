package wrime.ast;

import java.util.List;

public class TagImport extends WrimeTag {
    private final List<LocatableString> packagePath;
    private final LocatableString packageTarget;

    public TagImport(List<LocatableString> packagePath, LocatableString packageTarget) {
        super("import");
        this.packagePath = packagePath;
        this.packageTarget = packageTarget;
    }

    public List<LocatableString> getPackagePath() {
        return packagePath;
    }

    public LocatableString getPackageTarget() {
        return packageTarget;
    }

    public boolean isAllFromPackage() {
        return packageTarget == null;
    }
}
