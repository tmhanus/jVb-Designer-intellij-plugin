package designer.ui.properties.editor;

import com.intellij.openapi.project.Project;
import designer.ui.properties.renderer.PropertiesDialog;
import specification.Properties;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Created by Tomas Hanus on 4/10/2015.
 */
public class PropertiesCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private Properties currentProperties;
    private Properties oldProperties;
    private JButton button;
    private PropertiesDialog propertiesDialog;
    private Project project;
    protected static final String EDIT = "edit";

    public PropertiesCellEditor(Project project) {
        this.project = project;
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (EDIT.equals(actionEvent.getActionCommand())) {
            this.propertiesDialog = new PropertiesDialog(this.project, true, currentProperties);
            this.propertiesDialog.show();

            if (this.propertiesDialog.getExitCode() == 0) {
                this.currentProperties = propertiesDialog.getProperties();
            }
            fireEditingStopped(); //Make the renderer reappear.
        } else { //User pressed dialog's "OK" button.
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        currentProperties = (Properties) o;
        oldProperties = (Properties) o;
        return this.button;
    }

    @Override
    public Object getCellEditorValue() {
        return currentProperties;
    }

}
