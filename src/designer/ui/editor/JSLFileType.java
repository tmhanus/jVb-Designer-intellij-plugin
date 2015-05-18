package designer.ui.editor;

import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.javaee.ResourceRegistrarImpl;
import com.intellij.lang.xml.XMLLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *  Created by Tomas Hanus on 4/21/2015.
 */
public class JSLFileType extends XmlLikeFileType {
    public static final String DEFAULT_EXTENSION = "jsl";
    public static final JSLFileType INSTANCE = new JSLFileType();

    public JSLFileType() {
        super(XMLLanguage.INSTANCE);
        ResourceRegistrarImpl resourceRegistrar = new ResourceRegistrarImpl();
        resourceRegistrar.addStdResource("", "/designer/resources/jobXML_jVbEdit.xsd");
    }

    @NotNull
    @Override
    public String getName() {
        return "Job definition file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Job definition language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return new ImageIcon(getClass().getResource("/designer/resources/JslFileType2.png"));
    }

}
