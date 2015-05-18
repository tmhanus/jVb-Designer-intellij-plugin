package designer.ui.palette;

import com.intellij.openapi.project.Project;
import designer.ui.palette.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 *  Created by Tomas Hanusas on 06.01.15.
 */
public class PalettePanel extends JPanel {
    private final Project project;
    private PaletteComponent activePComponent;
    private JPanel elementPanel;

    private JPanel stepContainer;
    private JPanel sequenceContainter;
    private JPanel branchContainer;
    private JPanel transitionContainer;


    public PalettePanel(Project project) {
        super(false);
        this.project = project;
        this.setLayout(new BorderLayout());
        elementPanel = new JPanel();

        tryInit();

        JPanel topLeft = new JPanel();
        topLeft.setLayout(new BorderLayout());
        topLeft.add(elementPanel, BorderLayout.WEST);
        topLeft.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.add(topLeft, BorderLayout.NORTH);
    }

    // ************************************************************************
    // *                         COMPONENT INIT                               *

    public void tryInit() {
        ImageIcon image = new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/triangleRight.png"));

        elementPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ***************** STEP

        JLabel label = new JLabel("Step");
        label.setIcon(image);
        label.setPreferredSize(new Dimension(150, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        elementPanel.add(label, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0.2;
        elementPanel.add(new ChunkComponent(this), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        elementPanel.add(new BatchletComponent(this), gbc);
        gbc.weightx = 0.5;
        /////////////////////////////////////////
        label = new JLabel("Sequence");
        label.setIcon(image);
        label.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        elementPanel.add(label, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridwidth = 1;
        elementPanel.add(new FlowComponent(this), gbc);

        // ***************** BRANCH

        label = new JLabel("Branch");
        label.setIcon(image);
        label.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        elementPanel.add(label, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        elementPanel.add(new DecisionComponent(this), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        elementPanel.add(new SplitComponent(this), gbc);

        // ***************** BRANCH

        label = new JLabel("Transition");
        label.setIcon(image);
        label.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        elementPanel.add(label, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        elementPanel.add(new StartComponent(this), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        elementPanel.add(new StopComponent(this), gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        elementPanel.add(new EndComponent(this), gbc);
        gbc.gridx = 1;
        gbc.gridy = 8;
        elementPanel.add(new FailComponent(this), gbc);

    }


    /**
     * Store reference to Active Palette Component
     *
     * @param component
     */
    public void setActivePComponent(PaletteComponent component) {
        this.activePComponent = component;
    }

    public PaletteComponent getActivePComponent() {
        return this.activePComponent;
    }

}
