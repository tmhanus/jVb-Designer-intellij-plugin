package designer.ui.properties.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import designer.ui.properties.tableModel.NonEditablDefaultTableModel;
import org.jetbrains.annotations.Nullable;
import specification.ExceptionClasses;
import specification.ExcludeClass;
import specification.IncludeClass;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
public class ExceptionsCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private static final String EXCLUDE_CLASSES = "Exclude";
    private static final String INCLUDE_CLASSES = "Include";
    private ExceptionClasses currentExceptions;
    private JButton button;
    private ExceptionsDialog exceptionsDialog;
    private Project project;
    protected static final String EDIT = "edit";
    private Object[][] dataProperties;

    public ExceptionsCellEditor(Project project) {
        this.project = project;
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (EDIT.equals(actionEvent.getActionCommand())) {
            this.exceptionsDialog = new ExceptionsDialog(this.project, true);
            this.exceptionsDialog.show();

            if (this.exceptionsDialog.getExitCode() == 0) {
                this.currentExceptions = exceptionsDialog.getExceptionClasses();
            }
            fireEditingStopped(); //Make the renderer reappear.
        } else { //User pressed dialog's "OK" button.
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        currentExceptions = (ExceptionClasses) o;
        return this.button;
    }

    @Override
    public Object getCellEditorValue() {
        return currentExceptions;
    }

    private class ExceptionsDialog extends DialogWrapper {
        private Object[][] dataIncludes;
        private Object[][] dataExcludes;
        private String[] columnNames = {"Class"};
        private ExceptionClassesPanel includesPanel;
        private ExceptionClassesPanel excludesPanel;

        public ExceptionsDialog(@Nullable Project project, boolean canBeParent) {
            super(project, canBeParent);
            setTitle("Exceptions");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel exceptionsDialogPanel = new JPanel();
            exceptionsDialogPanel.setPreferredSize(new Dimension(450, 400));
            exceptionsDialogPanel.setLayout(new BoxLayout(exceptionsDialogPanel, BoxLayout.PAGE_AXIS));
            this.dataIncludes = null;
            if ((currentExceptions != null) && (currentExceptions.getIncludeClasses() != null)) {
                this.dataIncludes = new String[currentExceptions.getIncludeClasses().size()][1];
                for (int i = 0; i < currentExceptions.getIncludeClasses().size(); i++) {
                    this.dataIncludes[i][0] = currentExceptions.getIncludeClasses().get(i).getClassToInclude();
                }
            }
            this.dataExcludes = null;
            if ((currentExceptions != null) && (currentExceptions.getExcludeClasses() != null)) {
                this.dataExcludes = new String[currentExceptions.getExcludeClasses().size()][1];
                for (int i = 0; i < currentExceptions.getExcludeClasses().size(); i++) {
                    this.dataExcludes[i][0] = currentExceptions.getExcludeClasses().get(i).getClassToExclude();
                }
            }
            includesPanel = new ExceptionClassesPanel(dataIncludes, INCLUDE_CLASSES);
            exceptionsDialogPanel.add(includesPanel);
            excludesPanel = new ExceptionClassesPanel(dataExcludes, EXCLUDE_CLASSES);
            exceptionsDialogPanel.add(excludesPanel);

            return exceptionsDialogPanel;
        }

        @Override
        public void dispose() {
            super.dispose();
        }

        public ExceptionClasses getExceptionClasses() {
            List<IncludeClass> includeClassesList;
            List<ExcludeClass> excludeClassesList;
            if ((this.includesPanel.getClassesData() == null) || (this.includesPanel.getClassesData().size() == 0))
                includeClassesList = null;
            else {
                includeClassesList = new ArrayList<IncludeClass>();
                for (int i = 0; i < this.includesPanel.getClassesData().size(); i++) {
                    String classFromVector = this.includesPanel.getClassesData().get(i).toString();
                    includeClassesList.add(new IncludeClass(classFromVector.substring(1, classFromVector.length() - 1)));
                }
            }
            if ((this.excludesPanel.getClassesData() == null) || (this.excludesPanel.getClassesData().size() == 0))
                excludeClassesList = null;
            else {
                excludeClassesList = new ArrayList<ExcludeClass>();
                for (int i = 0; i < this.excludesPanel.getClassesData().size(); i++) {
                    String classFromVector = this.excludesPanel.getClassesData().get(i).toString();
                    excludeClassesList.add(new ExcludeClass(classFromVector.substring(1, classFromVector.length() - 1)));
                }
            }
            if ((includeClassesList == null) && (excludeClassesList == null)) return null;
            ExceptionClasses exceptionClasses = new ExceptionClasses();
            exceptionClasses.setIncludeClasses(includeClassesList);
            exceptionClasses.setExcludeClasses(excludeClassesList);
            return exceptionClasses;
        }

        public class ExceptionClassesPanel extends JPanel {
            private Object[][] data;
            private String exceptionClassType;
            private JBTable classesTable;

            public ExceptionClassesPanel(Object[][] data, String exceptionClassType) {
                this.data = data;
                this.exceptionClassType = exceptionClassType;
                initPanel();
            }

            public void initPanel() {
                this.setLayout(new BorderLayout());
                this.setBorder(BorderFactory.createTitledBorder(exceptionClassType + " classes"));

                DefaultTableModel defaultTableModel = new NonEditablDefaultTableModel(data, columnNames);
                this.classesTable = new JBTable(defaultTableModel);
                this.classesTable.setRowSelectionAllowed(true);
                this.classesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                JBScrollPane scrollPanel = new JBScrollPane(this.classesTable,
                        JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );
                this.add(scrollPanel);

                JButton addPropertyBtn = new JButton("Add Class");
                addPropertyBtn.addActionListener(new AddBtnActionListener(this.data, exceptionClassType));

                JButton editPropertyBtn = new JButton("Edit Class");
                editPropertyBtn.addActionListener(new editBtnActionListener(this.data));

                JButton removePropertyBtn = new JButton("Remove Class");
                removePropertyBtn.addActionListener(new removeBtnActionListener(this.data, exceptionClassType));

                JPanel addEditRemoveButtons = new JPanel();
                addEditRemoveButtons.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipadx = 20;
                gbc.ipady = 20;
                addEditRemoveButtons.add(addPropertyBtn, gbc);
                gbc.gridy++;
                addEditRemoveButtons.add(editPropertyBtn, gbc);
                gbc.gridy++;
                addEditRemoveButtons.add(removePropertyBtn, gbc);
                this.add(addEditRemoveButtons, BorderLayout.EAST);
            }

            public Vector getClassesData() {
                return ((DefaultTableModel) this.classesTable.getModel()).getDataVector();
            }

            private class AddBtnActionListener implements ActionListener {
                private Object[][] data;
                private String exceptionType;

                public AddBtnActionListener(Object[][] ddata, String exceptionType) {
                    super();
                    this.data = ddata;
                    this.exceptionType = exceptionType;
                }

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (exceptionType.equals(INCLUDE_CLASSES)) {
                        if (dataIncludes == null) data = null;
                        else {
                            data = new Object[dataIncludes.length][1];
                            System.arraycopy(dataIncludes, 0, data, 0, dataIncludes.length);
                        }
                    } else {
                        if (dataExcludes == null) data = null;
                        else {
                            data = new Object[dataExcludes.length][1];
                            System.arraycopy(dataExcludes, 0, data, 0, dataExcludes.length);
                        }
                    }

                    if (data == null) data = new Object[0][1];
                    Object[][] tempData = new Object[data.length + 1][1];
                    for (int i = 0; i < data.length; i++) {
                        tempData[i][0] = data[i][0];
                    }
                    JTextField nameField = new JTextField(20);
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Name:"));
                    myPanel.add(nameField);
                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Enter class name", JOptionPane.OK_CANCEL_OPTION);
                    if ((result == JOptionPane.OK_OPTION) && !(nameField.getText().equals("")) && !(nameField.getText().trim().equals(""))) {
                        tempData[data.length][0] = nameField.getText();
                        data = tempData;
                    }
                    if (exceptionType.equals(INCLUDE_CLASSES)) {
                        if (data == null) dataIncludes = null;
                        else {
                            dataIncludes = new Object[data.length][1];
                            System.arraycopy(data, 0, dataIncludes, 0, data.length);
                        }
                    } else {
                        if (data == null) dataExcludes = null;
                        else {
                            dataExcludes = new Object[data.length][1];
                            System.arraycopy(data, 0, dataExcludes, 0, data.length);
                        }
                    }
                    classesTable.setModel(new NonEditablDefaultTableModel(data, columnNames));
                }
            }

            private class editBtnActionListener implements ActionListener {
                private Object[][] data;

                public editBtnActionListener(Object[][] data) {
                    super();
                    this.data = data;
                }

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (classesTable.getSelectedRow() == -1) return;

                    JTextField nameField = new JTextField(20);
                    nameField.setText((String) classesTable.getValueAt(classesTable.getSelectedRow(), 0));
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Clase name:"));
                    myPanel.add(nameField);
                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Edit class name", JOptionPane.OK_CANCEL_OPTION);
                    if ((result == JOptionPane.OK_OPTION) && !(nameField.getText().equals(""))) {
                        data[classesTable.getSelectedRow()][0] = nameField.getText();
                        classesTable.setValueAt(nameField.getText(), classesTable.getSelectedRow(), 0);
                    }
                }
            }

            private class removeBtnActionListener implements ActionListener {
                private Object[][] data;
                private String exceptionType;

                public removeBtnActionListener(Object[][] data, String exceptionType) {
                    this.data = data;
                    this.exceptionType = exceptionType;
                }

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (exceptionType.equals(INCLUDE_CLASSES)) {
                        if (dataIncludes == null) data = null;
                        else {
                            data = new Object[dataIncludes.length][1];
                            System.arraycopy(dataIncludes, 0, data, 0, dataIncludes.length);
                        }
                    } else {
                        if (dataExcludes == null) data = null;
                        else {
                            data = new Object[dataExcludes.length][1];
                            System.arraycopy(dataExcludes, 0, data, 0, dataExcludes.length);
                        }
                    }
                    if (classesTable.getSelectedRow() == -1) return;
                    Object[][] tempProperties = new Object[data.length - 1][2];
                    int counter = 0;
                    for (int i = 0; i < data.length; i++) {
                        if (classesTable.getSelectedRow() != i) {
                            tempProperties[counter][0] = data[i][0];
                            counter++;
                        }
                    }
                    data = tempProperties;

                    if (exceptionType.equals(INCLUDE_CLASSES)) {
                        if (data == null) dataIncludes = null;
                        else {
                            dataIncludes = new Object[data.length][1];
                            System.arraycopy(data, 0, dataIncludes, 0, data.length);
                        }
                    } else {
                        if (data == null) dataExcludes = null;
                        else {
                            dataExcludes = new Object[data.length][1];
                            System.arraycopy(data, 0, dataExcludes, 0, data.length);
                        }
                    }

                    classesTable.setModel(new NonEditablDefaultTableModel(data, columnNames));
                }
            }
        }

    }
}
