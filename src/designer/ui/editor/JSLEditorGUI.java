package designer.ui.editor;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import specification.Job;
import specification.definitions.Definition;

import javax.swing.*;
import java.awt.*;

/**
 *  Created by Tomas Hanusas on 20.12.14.
 */
public class JSLEditorGUI extends JPanel {
    private CanvasToolbar canvasToolbar;
    private JSLCanvas myCanvas;
    private Project project;
    //private JPanel panel;

    JTextField field;
    JButton button;

    public JSLEditorGUI(Project project, JSLFileGraphicEditor jslFileGraphicEditor) {
        this.project = project;
        setLayout(new BorderLayout());

        //  ************* PLATNO *******************
        this.myCanvas = new JSLCanvas(this.project, jslFileGraphicEditor);
        //this.myCanvas.setBackground(Color.lightGray);
        this.add(myCanvas, BorderLayout.CENTER);

        this.canvasToolbar = new CanvasToolbar(this.myCanvas);
        this.canvasToolbar.setPreferredSize(new Dimension(this.getWidth(), 27));
        this.myCanvas.setCanvasToolbar(canvasToolbar);
        this.add(canvasToolbar, BorderLayout.NORTH);
        PsiFile psiFile;


        this.setVisible(true);

    }

    public void loadJobDiagram(Job job) {
        this.myCanvas.loadJobDiagram(job);
    }

    public Job getRootElement() {
        if (this.myCanvas == null) return null;
        return this.myCanvas.getRootElement();
    }

    public Definition getDiagramDefinion() {
        return this.myCanvas.getDiagramDefinition();
    }

    public void setDiagramDefinition(Definition definition) {
        this.myCanvas.setDiagramDefinition(definition);
    }

    public boolean isDiagramValid() {
        return this.myCanvas.isDiagramValid();
    }
}