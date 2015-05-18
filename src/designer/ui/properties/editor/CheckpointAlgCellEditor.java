package designer.ui.properties.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import designer.ui.properties.tableModel.NonEditablDefaultTableModel;
import org.jetbrains.annotations.Nullable;
import specification.CheckpointAlgorithm;
import specification.Properties;
import specification.Property;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */

public class CheckpointAlgCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private CheckpointAlgorithm currentCheckpointAlgorithm;
    private CheckpointAlgorithm oldCheckpointAlgorithm;
    private JButton button;
    private CheckpointAlgorithmDialog checkpointDialog;
    private Project project;
    protected static final String EDIT = "edit";

    public CheckpointAlgCellEditor(Project project) {
        this.project = project;
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (EDIT.equals(actionEvent.getActionCommand())) {
            this.checkpointDialog = new CheckpointAlgorithmDialog(this.project, true);
            this.checkpointDialog.show();

            if (this.checkpointDialog.getExitCode() == 0) {
                if (this.currentCheckpointAlgorithm == null)
                    this.currentCheckpointAlgorithm = new CheckpointAlgorithm();
                this.currentCheckpointAlgorithm.setRef(checkpointDialog.getAlgorithmReference());
                this.currentCheckpointAlgorithm.setProperties(checkpointDialog.getProperties());
            }
            fireEditingStopped(); //Make the renderer reappear.
        } else { //User pressed dialog's "OK" button.
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        currentCheckpointAlgorithm = (CheckpointAlgorithm) o;
        oldCheckpointAlgorithm = (CheckpointAlgorithm) o;
        return this.button;
    }

    @Override
    public Object getCellEditorValue() {
        return currentCheckpointAlgorithm;
    }

    private class CheckpointAlgorithmDialog extends DialogWrapper {
        private Object[][] dataProperties;
        private String algorithmReference;
        private JBTable propertiesTable;
        private JTextField referenceTextField;
        private String[] columnNames = {"Name", "Value"};


        public CheckpointAlgorithmDialog(@Nullable Project project, boolean canBeParent) {
            super(project, canBeParent);
            setTitle("Checkpoint Algorithm");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel checkpointDialogPanel = new JPanel();
            checkpointDialogPanel.setLayout(new BorderLayout());
            this.dataProperties = null;
            if ((currentCheckpointAlgorithm != null) && (currentCheckpointAlgorithm.getProperties() != null)) {
                this.dataProperties = new String[currentCheckpointAlgorithm.getProperties().getProperties().size()][2];
                for (int i = 0; i < currentCheckpointAlgorithm.getProperties().getProperties().size(); i++) {
                    this.dataProperties[i][0] = currentCheckpointAlgorithm.getProperties().getProperties().get(i).getName();
                    this.dataProperties[i][1] = currentCheckpointAlgorithm.getProperties().getProperties().get(i).getValue();
                }
            }
            DefaultTableModel dtm = new NonEditablDefaultTableModel(dataProperties, columnNames);
            // MIDDLE Table
            this.propertiesTable = new JBTable(dtm);
            propertiesTable.setRowSelectionAllowed(true);
            propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JBScrollPane sp = new JBScrollPane(this.propertiesTable,
                    JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );
            checkpointDialogPanel.add(sp, BorderLayout.CENTER);

            // Bottom Buttons
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
            checkpointDialogPanel.add(addEditRemoveButtons, BorderLayout.SOUTH);

            /// Top ReferencePanel
            JPanel editAlgorithmReferencePanel = new JPanel();
            final JLabel referenceLabel = new JLabel("Reference: ");
            this.referenceTextField = new JTextField(15);
            if (currentCheckpointAlgorithm != null) {
                referenceTextField.setText(currentCheckpointAlgorithm.getRef());
                algorithmReference = currentCheckpointAlgorithm.getRef();
            }
            referenceTextField.setEditable(false);
            JButton editReferenceBtn = new JButton("Edit");
            editReferenceBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JPanel myPanel = new JPanel();
                    JTextField refField = new JTextField(15);
                    if (referenceTextField != null) refField.setText(referenceTextField.getText());
                    myPanel.add(refField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Checkpoint algorithm reference", JOptionPane.OK_CANCEL_OPTION);
                    if ((result == JOptionPane.OK_OPTION) && !(refField.getText().equals("")) && !(refField.getText().trim().equals(""))) {
                        algorithmReference = refField.getText();
                        referenceTextField.setText(refField.getText());
                    }
                }
            });
            editAlgorithmReferencePanel.add(referenceLabel);
            editAlgorithmReferencePanel.add(referenceTextField);
            editAlgorithmReferencePanel.add(editReferenceBtn);
            checkpointDialogPanel.add(editAlgorithmReferencePanel, BorderLayout.NORTH);

            return checkpointDialogPanel;
        }

        @Override
        public void dispose() {
            super.dispose();
        }

        public Properties getProperties() {
            Properties properties = new Properties();
            if ((dataProperties == null) || (dataProperties.length == 0)) return null;
            for (int i = 0; i < dataProperties.length; i++) {
                properties.addProperty(new Property((String) dataProperties[i][0], (String) dataProperties[i][1]));
            }
            return properties;
        }

        public String getAlgorithmReference() {
            return this.algorithmReference;
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
                        "New Property", JOptionPane.OK_CANCEL_OPTION);
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
                        "Edit Property", JOptionPane.OK_CANCEL_OPTION);
                if ((result == JOptionPane.OK_OPTION) && !(nameField.getText().equals("")) && !(nameField.getText().equals(""))) {
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
}