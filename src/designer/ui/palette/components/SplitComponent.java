package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public class SplitComponent extends PaletteComponent {
    private static final String SPLIT_COMPONENT_TYPE = "Split";

    public SplitComponent(PalettePanel panel) {
        super(SPLIT_COMPONENT_TYPE, panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Split32.png")));
    }
}
