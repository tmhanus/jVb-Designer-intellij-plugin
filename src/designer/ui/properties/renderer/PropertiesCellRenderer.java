package designer.ui.properties.renderer;

import specification.Properties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class PropertiesCellRenderer extends DefaultTableCellRenderer {
    public PropertiesCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        if (value instanceof Properties) {
            String textToDisplay = "No properties set";

            Properties properties = (Properties) table.getModel().getValueAt(row, column);

            if ((properties != null) && (properties.getProperties() != null)) {
                if (properties.getProperties().size() == 1) {
                    textToDisplay = "1 property set";
                } else {
                    textToDisplay = Integer.toString(properties.getProperties().size()) + " properties set";
                }
            }
            ((JLabel) comp).setText(textToDisplay);
        }
        return comp;
    }
}
