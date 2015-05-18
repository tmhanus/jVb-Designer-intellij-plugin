package designer.ui.editor;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;


/**
 *  Created by Tomas Hanusas on 20.12.14.
 */
public class JSLFileGraphicEditorProvider implements ApplicationComponent, FileEditorProvider, DumbAware {
    /**
     * The editor component name.
     */
    private static final String NAME = "JSLGraphicEditorProvider";
    /**
     * The editor type id.
     */
    private static final String EDITOR_TYPE_ID = "JSL - Graph";

    public JSLFileGraphicEditorProvider() {
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return virtualFile.getFileType() instanceof JSLFileType;
    }

    /**
     * Creates and returns an editor for the specified file.
     *
     * @param project     the project
     * @param virtualFile the file to be loaded
     * @return the editor
     */

    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
//        return null;
        return new JSLFileGraphicEditor(project, virtualFile);
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {
        fileEditor.dispose();
    }

    @Override
    public FileEditorState readState(@NotNull Element element,
                                     @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState fileEditorState,
                           @NotNull Project project, @NotNull Element element) {
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "JSLGraphicEditor";
    }
}
