package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import designer.ui.editor.JSLCanvas;
import designer.ui.editor.element.Element;
import designer.ui.properties.PropertyTable;
import specification.EndTransition;
import specification.FailTransition;
import specification.StopTransition;
import specification.definitions.Definition;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 *  Created by Tomas Hanus on 4/18/2015.
 */
public class EndTransitionTableModel extends AbstractTableModel {
    private Definition definition;
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Element element;
    private PropertyTable propertyTable;
    private JSLCanvas jslCanvas;
    private String[] idCollisionIn = new String[1];

    public EndTransitionTableModel(Element element, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.element = element;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        if (element instanceof StopTransition) return 3;
        return 2;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row == 0) {
            if (col == 0) return new String("Id");
            if (col == 1) {
                return element.getId();
            }
        }
        if (row == 1) {
            if (col == 0) return new String("Exit status");
            if (col == 1) {
                if (element instanceof FailTransition) {
                    return ((FailTransition) element).getExitStatus();
                } else if (element instanceof EndTransition)
                    return ((EndTransition) element).getExitStatus();
                else
                    return ((StopTransition) element).getExitStatus();
            }
        } else if (row == 2 && element instanceof StopTransition) {
            if (col == 0) return new String("Restart");
            if (col == 1) return ((StopTransition) element).getRestart();
        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (row == 0) {
            if (jslCanvas.getElementFactory().isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else element.setId((String) value);
        } else if (row == 1) {
            if (element instanceof FailTransition)
                ((FailTransition) element).setExitStatus((String) value);
            else if (element instanceof EndTransition)
                ((EndTransition) element).setExitStatus((String) value);
            else ((StopTransition) element).setExitStatus((String) value);
        } else if (row == 2) {
            ((StopTransition) element).setRestart((String) value);
        }
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