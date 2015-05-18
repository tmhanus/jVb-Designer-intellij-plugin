package designer.ui.properties.renderer;

import specification.CheckpointAlgorithm;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class CheckpointAlgorithmCellRenderer extends DefaultTableCellRenderer {
    public CheckpointAlgorithmCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        if (value instanceof CheckpointAlgorithm) {
            String textToDisplay = "No algorithm set";

            CheckpointAlgorithm checkpointAlgorithm = (CheckpointAlgorithm) table.getModel().getValueAt(row, column);

            if (checkpointAlgorithm != null) {
                textToDisplay = "Reference: " + checkpointAlgorithm.getRef();
            }
            ((JLabel) comp).setText(textToDisplay);
        }
        return comp;
    }
}
