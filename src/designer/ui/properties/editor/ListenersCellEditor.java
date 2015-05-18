package designer.ui.properties.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import designer.ui.properties.tableModel.NonEditablDefaultTableModel;
import org.jetbrains.annotations.Nullable;
import specification.Listeners;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Created by Tomas Hanus on 4/10/2015.
 */
public class ListenersCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private Listeners currentProperties;
    private Listeners oldListeners;
    private JButton button;
    private ListenersDialog listenersDialog;
    private Project project;
    protected static final String EDIT = "edit";

    public ListenersCellEditor(Project project) {
        this.project = project;
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (EDIT.equals(actionEvent.getActionCommand())) {
            this.listenersDialog = new ListenersDialog(this.project, true);
            this.listenersDialog.show();

            if (this.listenersDialog.getExitCode() == 0) {
                this.currentProperties = listenersDialog.getListeners();
            }
            fireEditingStopped(); //Make the renderer reappear.
        } else { //User pressed dialog's "OK" button.
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        currentProperties = (Listeners) o;
        oldListeners = (Listeners) o;
        return this.button;
    }

    @Override
    public Object getCellEditorValue() {
        return currentProperties;
    }

    private class ListenersDialog extends DialogWrapper {
        private Object[][] dataListeners;
        private JBTable listenersTable;
        private String[] columnNames = {"Listener reference"};

        public ListenersDialog(@Nullable Project project, boolean canBeParent) {
            super(project, canBeParent);
            setTitle("Listeners");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel listenersDialogPanel = new JPanel();
            listenersDialogPanel.setLayout(new BorderLayout());
            this.dataListeners = null;
            if ((currentProperties != null) && (currentProperties.getListeners() != null)) {
                this.dataListeners = new String[currentProperties.getListeners().size()][1];
                for (int i = 0; i < currentProperties.getListeners().size(); i++) {
                    this.dataListeners[i][0] = currentProperties.getListeners().get(i).getRef();
                }
            }
            DefaultTableModel dtm = new NonEditablDefaultTableModel(dataListeners, columnNames);
            this.listenersTable = new JBTable(dtm);
            listenersTable.setRowSelectionAllowed(true);
            listenersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JBScrollPane sp = new JBScrollPane(this.listenersTable,
                    JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );
            listenersDialogPanel.add(sp, BorderLayout.NORTH);

            JButton addListenerBtn = new JButton("Add Listener");
            addListenerBtn.addActionListener(new addBtnActionListener());

            JButton editListenerBtn = new JButton("Edit Listener");
            editListenerBtn.addActionListener(new editBtnActionListener());

            JButton removeListenerBtn = new JButton("Remove Listener");
            removeListenerBtn.addActionListener(new removeBtnActionListener());

            JPanel addEditRemoveButtons = new JPanel(new FlowLayout());
            addEditRemoveButtons.add(addListenerBtn);
            addEditRemoveButtons.add(editListenerBtn);
            addEditRemoveButtons.add(removeListenerBtn);
            listenersDialogPanel.add(addEditRemoveButtons, BorderLayout.CENTER);

            return listenersDialogPanel;

        }

        @Override
        public void dispose() {
            super.dispose();
        }

        public Listeners getListeners() {
            if ((dataListeners == null) || (dataListeners.length == 0)) return null;
            Listeners listeners = new Listeners();
            for (int i = 0; i < dataListeners.length; i++) {
                listeners.addNewListener((String) dataListeners[i][0]);
            }
            return listeners;
        }

        private class addBtnActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (dataListeners == null) dataListeners = new Object[0][1];
                Object[][] tempListeners = new Object[dataListeners.length + 1][1];
                for (int i = 0; i < dataListeners.length; i++) {
                    tempListeners[i][0] = dataListeners[i][0];
                }
                String name = JOptionPane.showInputDialog("Enter Listener Reference:");
                if ((name != null) && !(name.equals("")) && !(name.trim().equals(""))) {
                    tempListeners[dataListeners.length][0] = name;
                    dataListeners = tempListeners;
                }
                listenersTable.setModel(new NonEditablDefaultTableModel(dataListeners, columnNames));
            }
        }

        private class editBtnActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listenersTable.getSelectedRow() == -1) return;
                String value = JOptionPane.showInputDialog("Enter new listenerRef:", listenersTable.getValueAt(listenersTable.getSelectedRow(), 0));
                if ((value != null) && !(value.equals("")) && !(value.trim().equals(""))) {
                    dataListeners[listenersTable.getSelectedRow()][0] = value;
                    listenersTable.setValueAt(value, listenersTable.getSelectedRow(), 0);
                }
            }
        }

        private class removeBtnActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listenersTable.getSelectedRow() == -1) return;
                Object[][] tempListeners = new Object[dataListeners.length - 1][1];
                int counter = 0;
                for (int i = 0; i < dataListeners.length; i++) {
                    if (listenersTable.getSelectedRow() != i) {
                        tempListeners[counter][0] = dataListeners[i][0];
                        counter++;
                    }
                }
                dataListeners = tempListeners;
                listenersTable.setModel(new NonEditablDefaultTableModel(dataListeners, columnNames));
            }
        }
    }
}
