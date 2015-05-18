package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanus on 4/15/2015.
 */
public class StopComponent extends PaletteComponent {

    private static final String STOP_COMPONENT_TYPE = "Stop";

    public StopComponent(PalettePanel panel) {
        super(STOP_COMPONENT_TYPE, panel);
    }

    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Stop32.png")));
    }
}