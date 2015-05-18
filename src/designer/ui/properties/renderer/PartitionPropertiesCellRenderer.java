package designer.ui.properties.renderer;

import specification.Properties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class PartitionPropertiesCellRenderer extends DefaultTableCellRenderer {
    public PartitionPropertiesCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        if (value instanceof ArrayList) {
            String textToDisplay = "No partitions set";

            List<Properties> properties = (ArrayList) table.getModel().getValueAt(row, column);

            if (properties != null) {
                int numberOfPartitions = 0;
                numberOfPartitions = properties.size();
                if (numberOfPartitions == 0)
                    textToDisplay = "No partition set";
                else if (numberOfPartitions == 1)
                    textToDisplay = "1 partition set";
                else {
                    textToDisplay = Integer.toString(numberOfPartitions) + " partitions set";
                }
            }

            ((JLabel) comp).setText(textToDisplay);
        }
        return comp;
    }
}
