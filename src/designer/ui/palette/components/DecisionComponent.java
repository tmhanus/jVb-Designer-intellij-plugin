package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public class DecisionComponent extends PaletteComponent {
    private static final String DECISION_COMPONENT_TYPE = "Decision";

    public DecisionComponent(PalettePanel panel) {
        super(DECISION_COMPONENT_TYPE, panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Decision32.png")));
    }


}
