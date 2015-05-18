package designer.ui.properties.renderer;

import specification.Listeners;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class ListenersCellRenderer extends DefaultTableCellRenderer {
    public ListenersCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        if (value instanceof Listeners) {
            String textToDisplay = "No listeners set";
            Listeners listeners = (Listeners) table.getModel().getValueAt(row, column);
            if ((listeners != null) && (listeners.getListeners() != null)) {
                if (listeners.getListeners().size() == 1) {
                    textToDisplay = "1 listener set";
                } else {
                    textToDisplay = Integer.toString(listeners.getListeners().size()) + " listeners set";
                }
            }
            ((JLabel) comp).setText(textToDisplay);
        }
        return comp;
    }
}
