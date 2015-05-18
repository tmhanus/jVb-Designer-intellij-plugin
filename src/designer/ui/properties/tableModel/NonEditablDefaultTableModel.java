package designer.ui.properties.tableModel;

import javax.swing.table.DefaultTableModel;

/**
 *  Created by Tomas Hanus on 4/11/2015.
 */
public class NonEditablDefaultTableModel extends DefaultTableModel {

    public NonEditablDefaultTableModel(Object[][] objects, Object[] objects1) {
        super(objects, objects1);
    }

    @Override
    public boolean isCellEditable(int row, int column) { // custom isCellEditable function
        return false;
    }

    ;
}
