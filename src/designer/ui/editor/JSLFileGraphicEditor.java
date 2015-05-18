package designer.ui.editor;

import codeGeneration.xml.DefinitionFileGenerator;
import codeGeneration.xml.JSLFileGenerator;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentFactory;
import designer.ui.editor.element.Element;
import designer.ui.palette.PalettePanel;
import designer.ui.properties.PropertiesPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import specification.*;
import specification.definitions.Definition;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanusas on 20.12.14.
 */
public class JSLFileGraphicEditor implements FileEditor {
    private JSLEditorGUI editorGUI;
    private VirtualFile file;
    private Project project;
    private JSLFileGenerator jslFileGenerator;
    private DefinitionFileGenerator definitionFileGenerator;

    // **********************************************
    // *************** CONSTRUCTORS *****************

    public JSLFileGraphicEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        this.editorGUI = new JSLEditorGUI(project, this);
        this.jslFileGenerator = new JSLFileGenerator(this.project, this.file);

        String defFileName = new String(file.getCanonicalPath().substring(0, file.getCanonicalPath().length() - 3) + "jsd");
        File defFile = new File(defFileName);
        VirtualFile virtualDefinitionFile = LocalFileSystem.getInstance().findFileByIoFile(defFile);

        this.definitionFileGenerator = new DefinitionFileGenerator(this.project, defFile);
    }

    public JSLFileGraphicEditor() {
    }

    public VirtualFile getFile() {
        return this.file;
    }

    /**
     * Perform semantic control over next references of job structure.
     *
     * @param job
     * @param errMsg
     * @return
     */
    public boolean areNextReferencesValid(Job job, String[] errMsg) {
        if (job == null) return true;
        if (job.getElements() == null) return true;
        List<Flow> flowElementsWithSubElements = new ArrayList<Flow>();
        for (Element element : job.getElements()) {
            if (element instanceof Decision) {
                List<Next> allNextTransitions = ((Decision) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (job.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Step) {
                List<Next> allNextTransitions = ((Step) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (job.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Flow) {
                List<Next> allNextTransitions = ((Flow) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (job.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Split) {
                String nextTransitionId = ((Split) element).getNextElementId();
                if (nextTransitionId != null)
                    if (job.getElement(nextTransitionId) == null) {
                        errMsg[0] = getErrMsg(element.getId(), nextTransitionId);
                        return false;
                    }
            }
        }
        flowElementsWithSubElements = getAllFlowsWithSubElements(job.getElements());

        if (flowElementsWithSubElements != null && flowElementsWithSubElements.size() > 0) {
            for (Flow flow : flowElementsWithSubElements) {
                if (!areInsideFlowNextReferencesValid(flow, errMsg)) return false;
            }
        }

        return true;
    }

    private List<Flow> getAllFlowsWithSubElements(List<Element> elements) {
        List<Flow> flowsToReturn = new ArrayList<Flow>();
        for (Element element : elements) {
            if (element instanceof Flow) {
                if (((Flow) element).getElements() != null) {
                    flowsToReturn.add((Flow) element);
                    flowsToReturn.addAll(getAllFlowsWithSubElements(((Flow) element).getElements()));
                }
            }
        }
        return flowsToReturn;
    }

    /**
     * Perform semantic control of all nested Flow elements. Can be called recursivelly.
     *
     * @param flow
     * @param errMsg
     * @return
     */
    public boolean areInsideFlowNextReferencesValid(Flow flow, String[] errMsg) {
        if (flow == null) return true;
        if (flow.getElements() == null) return true;
        for (Element element : flow.getElements()) {
            if (element instanceof Decision) {
                List<Next> allNextTransitions = ((Decision) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (flow.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Step) {
                List<Next> allNextTransitions = ((Step) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (flow.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Flow) {
                List<Next> allNextTransitions = ((Flow) element).getAllNextTransitions();
                if (allNextTransitions != null) {
                    for (Next next : allNextTransitions) {
                        if (flow.getElement(next.getTo()) == null) {
                            errMsg[0] = getErrMsg(element.getId(), next.getTo());
                            return false;
                        }
                    }
                }
            } else if (element instanceof Split) {
                String nextTransitionId = ((Split) element).getNextElementId();
                if (nextTransitionId != null)
                    if (flow.getElement(nextTransitionId) == null) {
                        errMsg[0] = getErrMsg(element.getId(), nextTransitionId);
                        return false;
                    }
            }
        }
        return true;
    }

    /**
     * Create Error msg if logix of next references was broke.
     */
    public String getErrMsg(String elementId, String transitionToElementId) {
        return new String("Element " + elementId + " can't transit to element " + transitionToElementId + ", because element doesn't exist in the same context!");
    }

    /**
     * Fire writing Job Structure into file when this method is called.
     */
    public void jobDiagramChanged() {
        try {
            this.jslFileGenerator.marshal(this.editorGUI.getRootElement());
//            this.definitionFileGenerator.marshal(this.editorGUI.getDiagramDefinion());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void writeChangesToDocument() {

    }

    // **********************************************
    // **************** METHODS *********************

    @NotNull
    @Override
    public JComponent getComponent() {
        return editorGUI;
    }

    public JSLEditorGUI getEditor() {
        return editorGUI;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return editorGUI;
    }

    @NotNull
    @Override
    public String getName() {
        return "Visual JSL";
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {
        //this.editorGUI = new JSLEditorGUI(project, this);
        PalettePanel palettePanel = new PalettePanel(project);
        PropertiesPanel propertiesPanel = new PropertiesPanel(project);
        ToolWindowManager twm = ToolWindowManager.getInstance(project);

        // ****************** REGISTER JSL PALETTE ******************
        twm.unregisterToolWindow("JSL Palette");
        final ToolWindow toolWindow = twm.registerToolWindow("JSL Palette", true, ToolWindowAnchor.RIGHT);
        toolWindow.setIcon(new ImageIcon(getClass().getClassLoader().getResource("/designer/resources/painter14.png")));
        if (!toolWindow.isVisible()) toolWindow.show(new Runnable() {
            @Override
            public void run() {
            }
        });
        toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(palettePanel, "", false));

        // ****************** REGISTER JSL PROPERTIES ******************

        twm.unregisterToolWindow("JSL Properties");
        final ToolWindow propertiesToolWindow = twm.registerToolWindow("JSL Properties", true, ToolWindowAnchor.RIGHT);
        propertiesToolWindow.setSplitMode(true, new Runnable() {
            public void run() {
            }
        });
        propertiesToolWindow.setIcon(new ImageIcon(getClass().getClassLoader().getResource("/designer/resources/setting4.png")));
        if (!propertiesToolWindow.isVisible()) propertiesToolWindow.show(new Runnable() {
            @Override
            public void run() {
            }
        });
        propertiesToolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(propertiesPanel, "", false));

        Job job = null;
        Definition definition = null;
//        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        URL url = getClass().getResource("jobXML_jVbEdit.xsd");
        if (FileDocumentManager.getInstance().getDocument(file).getText().equals("")) {
            ApplicationManager.getApplication().runWriteAction(
                    new Runnable() {
                        public void run() {
                            String content = new String("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                            content += "\n<job restartable=\"true\" version=\"1.0\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\">\n</job>";
                            FileDocumentManager.getInstance().getDocument(file).setText(content);
                        }
                    });
        }
        try {
            job = this.jslFileGenerator.unmarshal();
        } catch (JAXBException e) {
            e.printStackTrace();
            job = null;
        }

        try {
            definition = this.definitionFileGenerator.unmarshal();
        } catch (JAXBException e) {
            definition = null;
        }

        // has to be done before loadJobDiagram
        if (definition != null) {
            this.editorGUI.setDiagramDefinition(definition);
        }
        this.editorGUI.loadJobDiagram(job);
    }

    @Override
    public void deselectNotify() {
        ToolWindowManager.getInstance(project).unregisterToolWindow("JSL Palette");
        ToolWindowManager.getInstance(project).unregisterToolWindow("JSL Properties");

        if (this.editorGUI.isDiagramValid()) {
//            if ((this.editorGUI.getDiagramDefinion() != null) && (this.editorGUI.getDiagramDefinion().getAllElementSpec() != null)){
//                for (ElementSpec spec :this.editorGUI.getDiagramDefinion().getAllElementSpec()){
//                    System.out.println(spec.getElementId() + " ----- " + spec.isPartitionEnabled() );
//                }
//            }
            try {

                this.jslFileGenerator.marshal(this.editorGUI.getRootElement());
                this.definitionFileGenerator.marshal(this.editorGUI.getDiagramDefinion());
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
    }

}