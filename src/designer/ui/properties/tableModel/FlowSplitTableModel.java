package designer.ui.properties.tableModel;

import com.intellij.openapi.project.Project;
import designer.ui.editor.DesignerContext;
import designer.ui.editor.ElementFactory;
import designer.ui.editor.JSLCanvas;
import designer.ui.editor.element.Element;
import designer.ui.properties.PropertyTable;
import specification.Flow;
import specification.definitions.Definition;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class FlowSplitTableModel extends AbstractTableModel {
    private Definition definition;
    private ElementFactory elementFactory;
    private Project project;
    private final String[] columnNames = {"Name", "Value"};
    private Element element;
    private PropertyTable propertyTable;
    private JSLCanvas jslCanvas;
    private String[] idCollisionIn = new String[1];

    public FlowSplitTableModel(Element element, PropertyTable propertyTable, Project project, JSLCanvas jslCanvas) {
        this.element = element;
        this.propertyTable = propertyTable;
        this.jslCanvas = jslCanvas;
        this.elementFactory = jslCanvas.getElementFactory();
        this.propertyTable.setRowSelectionAllowed(true);
        this.project = project;
        this.definition = jslCanvas.getDiagramDefinition();

        this.propertyTable.getRowEditorModel().addEditorForRow(1, new DefaultCellEditor(new JCheckBox()));
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    @Override
    public int getRowCount() {
        return 1;
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
        }
//        else if (row == 1){
//            if (col == 0) return new String("Abstract");
//            if (col == 1) {
//                if (element instanceof Flow)
//                    return ((Flow) element).isAbstract();
//                return ((Split) element).isAbstract();
//            }
//        }else if (row == 2){
//            if (col == 0) return new String("Parent");
//            if (col == 1) {
//                if (element instanceof Flow)
//                    return ((Flow) element).getParentElement();
//                return ((Split) element).getParentElement();
//            }
//        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (row == 0) {
            if (elementFactory.isIdentifierAlreadyUsed((String) value, idCollisionIn) && !value.equals(getValueAt(row, col)))
                showIdCollisionWarning();
            else {
                this.elementFactory.updateElementId(this.element, (String) value);
                if (element instanceof Flow) {
                    DesignerContext dc = this.jslCanvas.getDesignerContext(element.getId());
                    if (dc != null) {
                        dc.setContextId((String) value);
                        jslCanvas.actualizeCanvasToolbar();
                    }
                }
                element.setId((String) value);
            }
        }
//        else if(row == 1) {
//            if (element instanceof Flow)
//                ((Flow) element).setIsAbstract((Boolean)value);
//            else ((Split) element).setIsAbstract((Boolean) value);
//        }
//        else if(row == 2) {
//            if (element instanceof Flow)
//                ((Flow) element).setParentElement((String) value);
//            else ((Split) element).setParentElement((String)value);
//        }
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

