package designer.ui.properties.renderer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import designer.ui.properties.tableModel.NonEditablDefaultTableModel;
import org.jetbrains.annotations.Nullable;
import specification.Properties;
import specification.Property;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Created by Tomas Hanus on 4/13/2015.
 */
public class PropertiesDialog extends DialogWrapper {

    private Object[][] dataProperties;
    private JBTable propertiesTable;
    private String[] columnNames = {"Name", "Value"};

    public PropertiesDialog(@Nullable Project project, boolean canBeParent, Properties properties) {
        super(project, canBeParent);
        setTitle("Properties");

        this.dataProperties = null;
        if ((properties != null) && (properties.getProperties() != null)) {
            this.dataProperties = new String[properties.getProperties().size()][2];
            for (int i = 0; i < properties.getProperties().size(); i++) {
                this.dataProperties[i][0] = properties.getProperties().get(i).getName();
                this.dataProperties[i][1] = properties.getProperties().get(i).getValue();
            }
        }
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel propertiesDialogPanel = new JPanel();
        propertiesDialogPanel.setLayout(new BorderLayout());

        DefaultTableModel dtm = new NonEditablDefaultTableModel(dataProperties, columnNames);
        this.propertiesTable = new JBTable(dtm);
        propertiesTable.setRowSelectionAllowed(true);
        propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JBScrollPane sp = new JBScrollPane(this.propertiesTable,
                JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        propertiesDialogPanel.add(sp, BorderLayout.CENTER);

        JButton addPropertyBtn = new JButton("Add Property");
        addPropertyBtn.addActionListener(new addBtnActionListener());

        JButton editPropertyBtn = new JButton("Edit Property");
        editPropertyBtn.addActionListener(new editBtnActionListener());

        JButton removePropertyBtn = new JButton("Remove Property");
        removePropertyBtn.addActionListener(new removeBtnActionListener());

        JPanel addEditRemoveButtons = new JPanel(new FlowLayout());
        addEditRemoveButtons.add(addPropertyBtn);
        addEditRemoveButtons.add(editPropertyBtn);
        addEditRemoveButtons.add(removePropertyBtn);
        propertiesDialogPanel.add(addEditRemoveButtons, BorderLayout.NORTH);

        return propertiesDialogPanel;

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public Properties getProperties() {
        if ((dataProperties == null) || (dataProperties.length == 0)) return null;
        Properties properties = new Properties();
        for (int i = 0; i < dataProperties.length; i++) {
            properties.addProperty(new Property((String) dataProperties[i][0], (String) dataProperties[i][1]));
        }
        return properties;
    }

    private class addBtnActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (dataProperties == null) dataProperties = new Object[0][2];
            Object[][] tempProperties = new Object[dataProperties.length + 1][2];
            for (int i = 0; i < dataProperties.length; i++) {
                tempProperties[i][0] = dataProperties[i][0];
                tempProperties[i][1] = dataProperties[i][1];
            }
            JTextField nameField = new JTextField(20);
            JTextField valueField = new JTextField(20);
            JPanel myPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            myPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            myPanel.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            myPanel.add(new JLabel("Value:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            myPanel.add(valueField, gbc);

            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Property editor", JOptionPane.OK_CANCEL_OPTION);
            if ((result == JOptionPane.OK_OPTION) && !(nameField.getText().equals("")) && !(nameField.getText().trim().equals(""))) {
                tempProperties[dataProperties.length][0] = nameField.getText();
                tempProperties[dataProperties.length][1] = valueField.getText();
                dataProperties = tempProperties;
            }
            propertiesTable.setModel(new NonEditablDefaultTableModel(dataProperties, columnNames));
        }
    }

    private class editBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (propertiesTable.getSelectedRow() == -1) return;

            JTextField nameField = new JTextField(20);
            JTextField valueField = new JTextField(20);
            nameField.setText((String) propertiesTable.getValueAt(propertiesTable.getSelectedRow(), 0));
            valueField.setText((String) propertiesTable.getValueAt(propertiesTable.getSelectedRow(), 1));
            JPanel myPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            myPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            myPanel.add(nameField, gbc);


            gbc.gridx = 0;
            gbc.gridy = 1;
            myPanel.add(new JLabel("Value:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            myPanel.add(valueField, gbc);
            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Property editor", JOptionPane.OK_CANCEL_OPTION);
            if ((result == JOptionPane.OK_OPTION) && !(nameField.getText().equals("")) && !(nameField.getText().trim().equals(""))) {
                dataProperties[propertiesTable.getSelectedRow()][0] = nameField.getText();
                dataProperties[propertiesTable.getSelectedRow()][1] = valueField.getText();
                propertiesTable.setValueAt(nameField.getText(), propertiesTable.getSelectedRow(), 0);
                propertiesTable.setValueAt(valueField.getText(), propertiesTable.getSelectedRow(), 1);
            }
        }
    }

    private class removeBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (propertiesTable.getSelectedRow() == -1) return;
            Object[][] tempProperties = new Object[dataProperties.length - 1][2];
            int counter = 0;
            for (int i = 0; i < dataProperties.length; i++) {
                if (propertiesTable.getSelectedRow() != i) {
                    tempProperties[counter][0] = dataProperties[i][0];
                    tempProperties[counter][1] = dataProperties[i][1];
                    counter++;
                }
            }
            dataProperties = tempProperties;
            propertiesTable.setModel(new NonEditablDefaultTableModel(dataProperties, columnNames));
        }
    }
}
