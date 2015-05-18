package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanusas on 27.01.15.
 */
public class FlowComponent extends PaletteComponent {
    public FlowComponent(PalettePanel panel) {
        super("Flow", panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Flow32.png")));
    }
}
