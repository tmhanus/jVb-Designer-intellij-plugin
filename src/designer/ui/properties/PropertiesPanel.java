package designer.ui.properties;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import designer.ui.editor.JSLCanvas;
import designer.ui.editor.element.Edge;
import designer.ui.editor.element.Element;
import designer.ui.palette.components.PaletteComponent;
import designer.ui.properties.core.notifier.SelectionChangedNotifier;
import designer.ui.properties.editor.RowEditorModel;
import designer.ui.properties.renderer.RowRendererModel;
import designer.ui.properties.tableModel.*;
import specification.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 *  Created by Tomas Hanusas on 29.01.15.
 */
public class PropertiesPanel extends JPanel {
    private Project project;
    private PaletteComponent activePComponent;
    private JPanel propertiesPanel;
    private PropertyTable propertyTable;
    private AbstractTableModel myPropertyTableModel;

    private MessageBusConnection myConnection;

    public PropertiesPanel(final Project project) {
        super(false);
        this.project = project;

        this.setLayout(new BorderLayout());
        this.propertiesPanel = new JPanel();
        GridLayout gridLayout = new GridLayout();
        this.propertiesPanel.setLayout(gridLayout);

        this.propertyTable = new PropertyTable();
        this.propertyTable.setRowSelectionAllowed(false);
        this.propertyTable.setColumnSelectionAllowed(false);
        RowEditorModel rowEditorModel = new RowEditorModel();
        RowRendererModel rowRendererModel = new RowRendererModel();
        this.propertyTable.setRowEditorModel(rowEditorModel);
        this.propertyTable.setRowRendererModel(rowRendererModel);

        JBScrollPane sp = new JBScrollPane(this.propertyTable,
                JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        this.propertiesPanel.add(sp);
        this.add(this.propertiesPanel, BorderLayout.CENTER);
        myConnection = project.getMessageBus().connect();
        myConnection.subscribe(SelectionChangedNotifier.CHANGE_ACTION_TOPIC, new SelectionChangedNotifier() {

            @Override
            public void selectionChanged(Object selectedObject, JSLCanvas jslCanvas) {
                propertyTable.clearEditors();
                propertyTable.clearRenderers();
                if (selectedObject instanceof Job) {
                    myPropertyTableModel = new JobTableModel((Job) selectedObject, propertyTable, project, jslCanvas);
                } else if (selectedObject instanceof Element) {
                    Element element = (Element) selectedObject;
                    if (element instanceof Step)
                        myPropertyTableModel = new StepTableModel((Step) element, propertyTable, project, jslCanvas);
                    else if ((element instanceof Flow) || (element instanceof Split))
                        myPropertyTableModel = new FlowSplitTableModel(element, propertyTable, project, jslCanvas);
                    else if (element instanceof Decision)
                        myPropertyTableModel = new DecisionTableModel(element, propertyTable, project, jslCanvas);
                    else if (element instanceof FailTransition || element instanceof EndTransition || element instanceof StopTransition)
                        myPropertyTableModel = new EndTransitionTableModel(element, propertyTable, project, jslCanvas);
                } else if (selectedObject instanceof Edge) {
                    myPropertyTableModel = new EdgeTableModel((Edge) selectedObject, propertyTable, project, jslCanvas);
                }
                if (myPropertyTableModel != null) {
                    propertyTable.setModel(myPropertyTableModel);
                }
            }
        });
    }
}