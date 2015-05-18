package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import designer.ui.editor.ElementFactory;
import designer.ui.editor.JSLCanvas;
import designer.ui.editor.element.Element;
import designer.ui.properties.PropertyTable;
import designer.ui.properties.editor.PropertiesCellEditor;
import designer.ui.properties.renderer.PropertiesCellRenderer;
import specification.Decision;
import specification.Properties;
import specification.definitions.Definition;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class DecisionTableModel extends AbstractTableModel {
    private Definition definition;
    private ElementFactory elementFactory;
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Element element;
    private PropertyTable propertyTable;
    private JSLCanvas jslCanvas;
    private String[] idCollisionIn = new String[1];

    public DecisionTableModel(Element element, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.element = element;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;

//        this.elementFactory = ElementFactory.getInstance();
        this.elementFactory = jslCanvas.getElementFactory();
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;
        this.definition = jslCanvas.getDiagramDefinition();

        setRenderers();
        setEditors();
    }

    private void setRenderers() {
        this.propertyTable.getRowRendererModel().addRendererForRow(2, new PropertiesCellRenderer());//Step Properties
    }

    private void setEditors() {
        this.propertyTable.getRowEditorModel().addEditorForRow(2, new PropertiesCellEditor(project));
    }


    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row == 0) {
            if (col == 0) return new String("Id");
            if (col == 1) return element.getId();
        } else if (row == 1) {
            if (col == 0) return new String("Reference");
            if (col == 1) return ((Decision) element).getRef();
        } else if (row == 2) {
            if (col == 0) return new String("Properties");
            if (col == 1) return ((Decision) element).getProperties();
        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (row == 0) {
            if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else element.setId((String) value);
        } else if (row == 1) {
            if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else ((Decision) element).setRef((String) value);
        } else if (row == 2) {
            ((Decision) element).setProperties((Properties) value);
        }
        this.jslCanvas.fireJobDiagramChange();
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

