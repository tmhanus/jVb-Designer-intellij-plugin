package designer.ui.properties.renderer;

import specification.ExceptionClasses;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class ExceptionsCellRenderer extends DefaultTableCellRenderer {

    public ExceptionsCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        ((JLabel) comp).setOpaque(true);
        if (value instanceof ExceptionClasses) {
            String textToDisplay = "No exceptions set";

            ExceptionClasses exceptionClasses = (ExceptionClasses) table.getModel().getValueAt(row, column);

            if (exceptionClasses != null) {
                if ((exceptionClasses.getExcludeClasses() != null) || (exceptionClasses.getIncludeClasses() != null)) {
                    int numberOfClasses = 0;
                    if (exceptionClasses.getIncludeClasses() != null)
                        numberOfClasses += exceptionClasses.getIncludeClasses().size();
                    if (exceptionClasses.getExcludeClasses() != null)
                        numberOfClasses += exceptionClasses.getExcludeClasses().size();
                    if (numberOfClasses == 0)
                        textToDisplay = "No exceptions set";
                    else if (numberOfClasses == 1)
                        textToDisplay = "1 class set";
                    else {
                        textToDisplay = Integer.toString(numberOfClasses) + " classes set";
                    }
                }


            }
            ((JLabel) comp).setText(textToDisplay);
        }
        return comp;
    }
}
