package designer.ui.properties.renderer;

import javax.swing.table.TableCellRenderer;
import java.util.Hashtable;

/**
 *  Created by Tomas Hanus on 4/10/2015.
 */
public class RowRendererModel {
    /**
     * HashTable that stores all programatically defined renderers. Stored couple (row, renderer)
     */
    private Hashtable data;

    public RowRendererModel() {
        this.data = new Hashtable();
    }

    public void addRendererForRow(int row, TableCellRenderer tableCellRenderer) {
        data.put(row, tableCellRenderer);
    }

    public void removeRendererForRow(int row) {
        data.remove(new Integer(row));
    }

    public void removeAllRenderers() {
        this.data.clear();
    }

    public TableCellRenderer getRenderer(int row) {
        return (TableCellRenderer) data.get(new Integer(row));
    }

    public void clearRenderers() {
        if (this.data != null) this.data.clear();
    }
}
