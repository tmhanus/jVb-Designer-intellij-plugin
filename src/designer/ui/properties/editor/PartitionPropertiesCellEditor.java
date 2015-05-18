package designer.ui.properties.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import designer.ui.properties.renderer.PropertiesDialog;
import designer.ui.properties.tableModel.NonEditablDefaultTableModel;
import org.jetbrains.annotations.Nullable;
import specification.Properties;
import specification.Property;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
public class PartitionPropertiesCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private List<Properties> currentPartitionProperties;
    private List<Properties> oldPartitionProperties;
    private JButton button;
    private PartitionPropertiesDialog partitionPropertiesDialog;
    private Project project;
    protected static final String EDIT = "edit";

    public PartitionPropertiesCellEditor(Project project) {
        this.project = project;
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (EDIT.equals(actionEvent.getActionCommand())) {
            this.partitionPropertiesDialog = new PartitionPropertiesDialog(this.project, true);
            this.partitionPropertiesDialog.show();

            if (this.partitionPropertiesDialog.getExitCode() == 0) {
                this.currentPartitionProperties = partitionPropertiesDialog.getPartitionProperties();
                namePartitions(this.currentPartitionProperties);
            } else {
                this.currentPartitionProperties = oldPartitionProperties;
            }
            fireEditingStopped(); //Make the renderer reappear.
        } else { //User pressed dialog's "OK" button.
        }
    }

    private void namePartitions(List<Properties> properties) {
        if (properties != null && properties.size() > 0) {
            for (int i = 0; i < properties.size(); i++) {
                properties.get(i).setPartitionName(String.valueOf(i));
            }
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        currentPartitionProperties = (List<Properties>) o;
        oldPartitionProperties = clonePartitionProperties(currentPartitionProperties);//new ArrayList<Properties>(currentPartitionProperties);

        if (currentPartitionProperties == null) currentPartitionProperties = new ArrayList<Properties>();
        return this.button;
    }

    private List<Properties> clonePartitionProperties(List<Properties> propertiesToClone) {
        if (propertiesToClone == null) return null;
        if (propertiesToClone.size() == 0) return new ArrayList<Properties>();

        List<Properties> newPropertiesList = new ArrayList<Properties>();
        for (Properties prop : propertiesToClone) {
            if (prop.getProperties() == null) newPropertiesList.add(new Properties());
            Properties newProperties = new Properties();
            if (prop.getProperties().size() != 0) {
                for (Property p : prop.getProperties()) {
                    newProperties.addProperty(new Property(new String(p.getName()), new String(p.getValue())));
                }
                newPropertiesList.add(newProperties);
            }
        }
        return newPropertiesList;
    }

    @Override
    public Object getCellEditorValue() {
        return currentPartitionProperties;
    }

    private class PartitionPropertiesDialog extends DialogWrapper {
        private Object[][] dataPartitionProperties;
        private String[] columnNames = {"Partition", "Properties"};
        private JTable partitionPropertiesTable;

        public PartitionPropertiesDialog(@Nullable Project project, boolean canBeParent) {
            super(project, canBeParent);
            setTitle("Partition plan Properties");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel partitionPropertiesPanel = new JPanel();
            partitionPropertiesPanel.setPreferredSize(new Dimension(450, 400));
            partitionPropertiesPanel.setLayout(new BorderLayout());
            this.dataPartitionProperties = null;
            if (currentPartitionProperties != null) {
                this.dataPartitionProperties = new Object[currentPartitionProperties.size()][2];
                for (int i = 0; i < currentPartitionProperties.size(); i++) {
                    this.dataPartitionProperties[i][0] = new String("Partition" + i);
                    String propertiesToDisplay = null;
                    if ((currentPartitionProperties.get(i).getProperties() != null) && (currentPartitionProperties.get(i).getProperties().size() != 0)) {
                        for (Property property : currentPartitionProperties.get(i).getProperties()) {
                            if (propertiesToDisplay == null) propertiesToDisplay = new String(property.getName());
                            else propertiesToDisplay = propertiesToDisplay + ", " + property.getName();
                        }
                    }
                    this.dataPartitionProperties[i][1] = propertiesToDisplay;
                }
            }

            DefaultTableModel dtm = new NonEditablDefaultTableModel(dataPartitionProperties, columnNames);
            this.partitionPropertiesTable = new JBTable(dtm);
            partitionPropertiesTable.setRowSelectionAllowed(true);
            partitionPropertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JBScrollPane scrollPanel = new JBScrollPane(this.partitionPropertiesTable,
                    JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );
            partitionPropertiesPanel.add(scrollPanel, BorderLayout.CENTER);

            JButton addPartitionBtn = new JButton("Add Partition");
            addPartitionBtn.addActionListener(new AddPartitionBtnActionListener());

            JButton editPartitionBtn = new JButton("Edit Partition");
            editPartitionBtn.addActionListener(new EditPartitionBtnActionListener());

            JButton removePartitionBtn = new JButton("Remove Partition");
            removePartitionBtn.addActionListener(new RemovePartitionBtnActionListener());

            JPanel addEditRemoveButtons = new JPanel(new FlowLayout());
            addEditRemoveButtons.add(addPartitionBtn);
            addEditRemoveButtons.add(editPartitionBtn);
            addEditRemoveButtons.add(removePartitionBtn);

            partitionPropertiesPanel.add(addEditRemoveButtons, BorderLayout.NORTH);


            return partitionPropertiesPanel;
        }

        @Override
        public void dispose() {
            super.dispose();
        }

        public List<Properties> getPartitionProperties() {
            if ((currentPartitionProperties == null) || (currentPartitionProperties.size() == 0)) return null;
            return currentPartitionProperties;
        }


        private class AddPartitionBtnActionListener implements ActionListener {
            private Object[][] data;

            public AddPartitionBtnActionListener() {
                super();
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (dataPartitionProperties == null) dataPartitionProperties = new Object[0][2];
                Object[][] tempData = new Object[dataPartitionProperties.length + 1][2];
                for (int i = 0; i < dataPartitionProperties.length; i++) {
                    tempData[i][0] = dataPartitionProperties[i][0];
                    tempData[i][1] = dataPartitionProperties[i][1];
                }

                Properties newProperties = new Properties();

                PropertiesDialog propertiesDialog = new PropertiesDialog(project, true, newProperties);
                propertiesDialog.show();

                if (propertiesDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                    newProperties = propertiesDialog.getProperties();
                    if ((newProperties != null) && (newProperties.getProperties() != null) && (newProperties.getProperties().size() != 0)) {
                        tempData[dataPartitionProperties.length][0] = new String("Partition" + dataPartitionProperties.length);
                        String propertiesToDisplay = null;
                        if ((newProperties.getProperties() != null) && (newProperties.getProperties().size() != 0)) {
                            for (Property property : newProperties.getProperties()) {
                                if (propertiesToDisplay == null) propertiesToDisplay = new String(property.getName());
                                else propertiesToDisplay = propertiesToDisplay + ", " + property.getName();
                            }
                        }
                        tempData[dataPartitionProperties.length][1] = propertiesToDisplay;
                        dataPartitionProperties = tempData;
                        currentPartitionProperties.add(newProperties);

                    }
                }

                partitionPropertiesTable.setModel(new NonEditablDefaultTableModel(dataPartitionProperties, columnNames));
            }
        }

        private class EditPartitionBtnActionListener implements ActionListener {

            public EditPartitionBtnActionListener() {
                super();
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (partitionPropertiesTable.getSelectedRow() == -1) return;

                PropertiesDialog propertiesDialog = new PropertiesDialog(project, true, currentPartitionProperties.get(partitionPropertiesTable.getSelectedRow()));
                propertiesDialog.show();

                if (propertiesDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                    Properties editedProperties = propertiesDialog.getProperties();
                    if ((editedProperties == null) || (editedProperties.getProperties() == null) || (editedProperties.getProperties().size() == 0)) {
                        dataPartitionProperties[partitionPropertiesTable.getSelectedRow()][1] = new String("");
                    } else {
                        String propertiesToDisplay = null;
                        if ((editedProperties.getProperties() != null) && (editedProperties.getProperties().size() != 0)) {
                            for (Property property : editedProperties.getProperties()) {
                                if (propertiesToDisplay == null) propertiesToDisplay = new String(property.getName());
                                else propertiesToDisplay = propertiesToDisplay + ", " + property.getName();
                            }
                        }
                        dataPartitionProperties[partitionPropertiesTable.getSelectedRow()][1] = propertiesToDisplay;
                    }
                    if (editedProperties == null)
                        currentPartitionProperties.get(partitionPropertiesTable.getSelectedRow()).setProperties(null);
                    else
                        currentPartitionProperties.get(partitionPropertiesTable.getSelectedRow()).setProperties(editedProperties.getProperties());
                    partitionPropertiesTable.setModel(new NonEditablDefaultTableModel(dataPartitionProperties, columnNames));
                }

            }
        }

        private class RemovePartitionBtnActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (partitionPropertiesTable.getSelectedRow() == -1) return;
                Object[][] tempProperties = new Object[dataPartitionProperties.length - 1][2];
                int counter = 0;
                boolean removing = false;
                for (int i = 0; i < dataPartitionProperties.length; i++) {
                    if (partitionPropertiesTable.getSelectedRow() == i) removing = true;
                    if (partitionPropertiesTable.getSelectedRow() != i) {
                        if (removing == true)
                            tempProperties[counter][0] = new String("Partition" + (i - 1));
                        else
                            tempProperties[counter][0] = dataPartitionProperties[i][0];
                        tempProperties[counter][1] = dataPartitionProperties[i][1];
                        counter++;
                    }
                }
                currentPartitionProperties.remove(partitionPropertiesTable.getSelectedRow());
                dataPartitionProperties = tempProperties;
                partitionPropertiesTable.setModel(new NonEditablDefaultTableModel(dataPartitionProperties, columnNames));
            }
        }

    }
}
