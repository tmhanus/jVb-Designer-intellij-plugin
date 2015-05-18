package designer.ui.properties.renderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/11/2015.
 */
public class CategoryRowRenderer extends DefaultTableCellRenderer {
    public CategoryRowRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        ((JLabel) comp).setForeground(Color.LIGHT_GRAY);

        comp.setBackground(new Color(85, 90, 93));
        return comp;
    }
}
