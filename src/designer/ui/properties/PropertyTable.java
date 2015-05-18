package designer.ui.properties;

import designer.ui.properties.editor.RowEditorModel;
import designer.ui.properties.renderer.RowRendererModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Vector;

/**
 *  Created by Tomas Hanus on 3/13/2015.
 */
public class PropertyTable extends JTable {
    private RowEditorModel rowEditorModel;
    private RowRendererModel rowRendererModel;
    private PropertiesPanel propertiesPanel;

    public PropertyTable() {
    }

    public PropertyTable(TableModel tableModel) {
        super(tableModel);
    }

    public PropertyTable(TableModel tableModel, TableColumnModel tableColumnModel) {
        super(tableModel, tableColumnModel);
    }

    public PropertyTable(TableModel tableModel, TableColumnModel tableColumnModel, ListSelectionModel listSelectionModel) {
        super(tableModel, tableColumnModel, listSelectionModel);
    }

    public PropertyTable(int i, int i1) {
        super(i, i1);
    }

    public PropertyTable(Vector vector, Vector vector1) {
        super(vector, vector1);
    }

    public PropertyTable(Object[][] objects, Object[] objects1) {
        super(objects, objects1);
    }

    public PropertyTable(TableModel tableModel, RowEditorModel rowEditorModel) {
        super(tableModel, null, null);
        this.rowEditorModel = rowEditorModel;
    }


    public void setRowEditorModel(RowEditorModel rowEditorModel) {
        this.rowEditorModel = rowEditorModel;
    }

    public RowEditorModel getRowEditorModel() {
        return this.rowEditorModel;
    }

    public RowRendererModel getRowRendererModel() {
        return rowRendererModel;
    }

    public void setRowRendererModel(RowRendererModel rowRendererModel) {
        this.rowRendererModel = rowRendererModel;
    }

    public void clearRenderers() {
        if (this.getRowEditorModel() != null)
            this.getRowRendererModel().clearRenderers();
    }

    public void clearEditors() {
        if (this.getRowEditorModel() != null)
            this.getRowEditorModel().clearEditors();
    }

    /**
     * Overriden method getCellEditor returns editor defined by programmer if exists (for example PropertiesCellEditor),
     * or let the super class to decide which editor use
     *
     * @param row
     * @param col
     * @return
     */
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor wantedEditor = null;
        if (this.rowEditorModel != null) {
            wantedEditor = this.rowEditorModel.getEditor(row);
        }
        if (wantedEditor != null)
            return wantedEditor;
        return super.getCellEditor(row, col);
    }

    /**
     * Overriden method getCellRenderer returns editor defined by programmer if exists (for example PropertiesCellEditor),
     * or let the super class to decide which renderer use
     *
     * @param row
     * @param column
     * @return
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer wantedRenderer = null;
        if (this.rowEditorModel != null) {
            wantedRenderer = this.rowRendererModel.getRenderer(row);
        }
        if (wantedRenderer != null) return wantedRenderer;

        if (getColumnName(column) == "Value") {
            Class cellClass = null;
            if (getValueAt(row, column) != null)
                cellClass = getValueAt(row, column).getClass();
            if (cellClass != null)
                return getDefaultRenderer(cellClass);
            //wantedRenderer = this.rowRendererModel.getRenderer(row);
        }
        if (wantedRenderer != null)
            return wantedRenderer;
        return super.getCellRenderer(row, column);
    }
}
