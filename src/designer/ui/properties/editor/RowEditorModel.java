package designer.ui.properties.editor;

import javax.swing.table.TableCellEditor;
import java.util.Hashtable;

/**
 *  Created by Tomas Hanus on 4/10/2015.
 */
public class RowEditorModel {
    /**
     * HashTable that stores all programatically defined editors. Stored couple (row, editor)
     */
    private Hashtable data;

    public RowEditorModel() {
        this.data = new Hashtable();
    }

    public void addEditorForRow(int row, TableCellEditor tableCellEditor) {
        data.put(row, tableCellEditor);
    }

    public void removeEditorForRow(int row) {
        data.remove(new Integer(row));
    }

    public void removeAllEditors() {
        this.data.clear();
    }

    public TableCellEditor getEditor(int row) {
        return (TableCellEditor) data.get(new Integer(row));
    }

    public void clearEditors() {
        if (this.data != null)
            this.data.clear();
    }
}
