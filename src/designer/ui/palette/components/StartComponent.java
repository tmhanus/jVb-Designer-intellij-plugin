package designer.ui.palette.components;

import designer.ui.palette.PalettePanel;

import javax.swing.*;

/**
 *  Created by Tomas Hanus on 3/30/2015.
 */
public class StartComponent extends PaletteComponent {

    private static final String START_COMPONENT_TYPE = "Start";

    public StartComponent(PalettePanel panel) {
        super(START_COMPONENT_TYPE, panel);
    }


    public void setIcon(Icon icon) {
        super.setIcon(new ImageIcon(getClass().getResource("/designer/resources/paletteIcons/Start32.png")));
    }
}