package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import designer.ui.editor.DesignerContext;
import designer.ui.editor.JSLCanvas;
import designer.ui.properties.PropertyTable;
import designer.ui.properties.editor.ListenersCellEditor;
import designer.ui.properties.editor.PropertiesCellEditor;
import designer.ui.properties.renderer.ListenersCellRenderer;
import designer.ui.properties.renderer.PropertiesCellRenderer;
import specification.Job;
import specification.Listeners;
import specification.Properties;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 *  Created by Tomas Hanus on 4/29/2015.
 */
public class JobTableModel extends AbstractTableModel {
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Job job;
    private PropertyTable propertyTable;
    private JSLCanvas jslCanvas;
    private String[] idCollisionIn = new String[1];

    public JobTableModel(Job job, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.job = job;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;

        setRenderers();
        setEditors();
    }

    private void setRenderers() {
        this.propertyTable.getRowRendererModel().addRendererForRow(2, new ListenersCellRenderer());//Job Listeners
        this.propertyTable.getRowRendererModel().addRendererForRow(3, new PropertiesCellRenderer());//Job Properties
    }

    private void setEditors() {
        this.propertyTable.getRowEditorModel().addEditorForRow(1, new DefaultCellEditor(new JCheckBox()));
        this.propertyTable.getRowEditorModel().addEditorForRow(2, new ListenersCellEditor(project));
        this.propertyTable.getRowEditorModel().addEditorForRow(3, new PropertiesCellEditor(project));
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        return 4;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row == 0) {
            if (col == 0) return new String("Id");
            if (col == 1) return job.getId();
        } else if (row == 1) {
            if (col == 0) return new String("Restartable");
            if (col == 1) return job.isRestartable();
        } else if (row == 2) {
            if (col == 0) return new String("Listeners");
            if (col == 1) return job.getListeners();
        } else if (row == 3) {
            if (col == 0) return new String("Properties");
            if (col == 1) return job.getProperties();
        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (row == 0) {
            if (jslCanvas.getElementFactory().isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else {
                DesignerContext dc = this.jslCanvas.getDesignerContext(job.getId());
                if (dc != null) {
                    dc.setContextId((String) value);
                    jslCanvas.actualizeCanvasToolbar();
                }
                job.setId((String) value);
            }
        } else if (row == 1) {
            job.setRestartable((Boolean) value);
        } else if (row == 2) {
            job.setListeners((Listeners) value);
        } else if (row == 3) {
            job.setProperties((Properties) value);
        }
        fireTableCellUpdated(row, col);
        this.jslCanvas.fireJobDiagramChange();
        this.jslCanvas.actualizeCanvasToolbar();
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 1) return true;
        return false;
    }

    private void showIdCollisionWarning() {
        JOptionPane.showMessageDialog(this.propertyTable,
                "This identifier is already used in element " + this.idCollisionIn[0] + "!",
                "Identifier conflict",
                JOptionPane.WARNING_MESSAGE);
    }
}