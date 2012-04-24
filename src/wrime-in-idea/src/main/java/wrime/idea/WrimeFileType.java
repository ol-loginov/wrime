package wrime.idea;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnusedDeclaration")
public class WrimeFileType extends LanguageFileType {
    private static final Icon ICON = IconLoader.getIcon("/wrime/idea/wrimeFile.png");

    @NonNls
    public static final String DEFAULT_EXTENSION = "wrime";
    @NonNls
    public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    private WrimeFileType() {
        super(PlainTextLanguage.INSTANCE);
    }

    @Override
    @NotNull
    public String getName() {
        return "Wrime Template";
    }

    @NotNull
    @Override
    public String getDescription() {
        return WrimeBundle.message("wrime.files.plain.type.description");
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DOT_DEFAULT_EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
}
