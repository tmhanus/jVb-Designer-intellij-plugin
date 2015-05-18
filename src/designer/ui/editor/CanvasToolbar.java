package designer.ui.editor;

import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *  Created by Tomas Hanus on 4/29/2015.
 */
public class CanvasToolbar extends JPanel {
    private ComboBox designerContextsComboBox;
    private DesignerContexItemListener designerContexItemListener;

    private JSLCanvas jslCanvas;

    public CanvasToolbar(JSLCanvas jslCanvas) {
        this.jslCanvas = jslCanvas;
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(new EmptyBorder(1, 15, 0, 10));
        initToolbarComponents();
        this.add(Box.createRigidArea(new Dimension(2000, 0)));
    }

    public void initToolbarComponents() {
        JLabel activeContextLabel = new JLabel("Active context: ");
        designerContextsComboBox = new ComboBox();

        designerContextsComboBox.setPreferredSize(new Dimension(250, 15));
        this.designerContexItemListener = new DesignerContexItemListener();

        for (DesignerContext dc : jslCanvas.getDesignerContexts()) {
            designerContextsComboBox.addItem(dc.getContextId());
        }

        JButton goBackButton = new JButton("  Upper Context");
        goBackButton.setIcon(new ImageIcon(getClass().getResource("/designer/resources/UpperContext.png")));

        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!jslCanvas.getActiveContext().getContextId().equals(jslCanvas.getRootElement().getId())) {
                    if (jslCanvas.getActiveContext().getParentContext() != null) {
                        jslCanvas.setActiveContext(jslCanvas.getActiveContext().getParentContext());
                        actualizeContent();
                    }
                    jslCanvas.repaint();
                }
            }
        });

        this.add(activeContextLabel);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(designerContextsComboBox);
        this.add(Box.createVerticalStrut(10));
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(goBackButton);
    }


    /**
     * Actualize content of toolbar in order to display all actual designer contexts in comboBox component.
     */
    public void actualizeContent() {
        this.designerContextsComboBox.removeAllItems();
        this.designerContextsComboBox.removeItemListener(this.designerContexItemListener);
        for (DesignerContext dc : jslCanvas.getDesignerContexts()) {
            designerContextsComboBox.addItem(dc.getContextId());
        }
        if (jslCanvas.getActiveContext() != null)
            this.designerContextsComboBox.setSelectedItem(jslCanvas.getActiveContext().getContextId());
        designerContextsComboBox.addItemListener(this.designerContexItemListener);
    }

    private class DesignerContexItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (designerContextsComboBox.getSelectedItem() != null)
                jslCanvas.changeContext(designerContextsComboBox.getSelectedItem().toString());
        }
    }
}
